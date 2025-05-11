package com.gcashapp;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

public class UserAuthentication {
    private Map<String, User> users;
    private CheckBalance checkBalance;
    private CashIn cashIn;
    private static final String USER_DATA_FILE = "C:\\Users\\javp1\\OneDrive\\Desktop\\GCashApp\\src\\com\\gcashapp\\user_data.txt";
    private static final String EMAIL_REGEX = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
    private static final String NUMBER_REGEX = "\\d{10}";
    private static final String PIN_REGEX = "\\d{4}";
    private static final String DEFAULT_ADMIN_EMAIL = "admin@jeffalden.com";
    private static final String DEFAULT_ADMIN_PIN = "1465";
    private static final String DEFAULT_ADMIN_ID = "10001";

    private static class User {
        String id;
        String name;
        String email;
        String number;
        String pin;
        boolean isAdmin;

        User(String id, String name, String email, String number, String pin, boolean isAdmin) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.number = number;
            this.pin = pin;
            this.isAdmin = isAdmin;
        }
    }

    public UserAuthentication(CheckBalance checkBalance, CashIn cashIn) {
        this.checkBalance = checkBalance;
        this.cashIn = cashIn;
        users = new HashMap<>();
        loadUserData();
        initializeDefaultAdmin();
        ensureDefaultAccounts();
    }

    private void initializeDefaultAdmin() {
        if (!users.containsKey(DEFAULT_ADMIN_EMAIL)) {
            users.put(DEFAULT_ADMIN_EMAIL, new User(DEFAULT_ADMIN_ID, "Default Admin", DEFAULT_ADMIN_EMAIL, "0000000000", DEFAULT_ADMIN_PIN, true));
            saveUserData();
            checkBalance.addAccount(DEFAULT_ADMIN_ID, 1000.00, DEFAULT_ADMIN_ID);
            cashIn.cashIn(DEFAULT_ADMIN_ID, 1000.00);
            System.out.println("Default user with admin rights created: Email: " + DEFAULT_ADMIN_EMAIL + ", PIN: " + DEFAULT_ADMIN_PIN + ", ID: " + DEFAULT_ADMIN_ID);
        }
    }

    private void ensureDefaultAccounts() {
        for (User user : users.values()) {
            if (!checkBalance.accountExists(user.id)) {
                checkBalance.addAccount(user.id, 0.00, user.id);
                cashIn.cashIn(user.id, 0.00);
                System.out.println("Recreated default bank account for user: Account Number: " + user.id + ", Initial Balance: $0.00");
            }
        }
    }

    public String register(String name, String email, String number, String pin, boolean isAdmin, String adminId) {
        if (name == null || name.trim().isEmpty()) {
            return "Name cannot be empty.";
        }
        if (!Pattern.matches(EMAIL_REGEX, email)) {
            return "Invalid email format.";
        }
        if (users.containsKey(email)) {
            return "Email already registered.";
        }
        if (!Pattern.matches(NUMBER_REGEX, number)) {
            return "Number must be exactly 10 digits.";
        }
        if (!Pattern.matches(PIN_REGEX, pin)) {
            return "PIN must be exactly 4 digits.";
        }
        if (isAdmin && (adminId == null || !users.containsKey(adminId) || !users.get(adminId).isAdmin)) {
            return "Only admins can register new admins.";
        }

        int maxId = users.values().stream()
                .mapToInt(user -> Integer.parseInt(user.id))
                .max()
                .orElse(10000);
        String newId = String.valueOf(maxId + 1);
        users.put(email, new User(newId, name, email, number, pin, isAdmin));
        saveUserData();

        checkBalance.addAccount(newId, 0.00, newId);
        cashIn.cashIn(newId, 0.00);
        checkBalance.forceSaveAccountData(); // Force save to ensure account data is persisted
        System.out.println("Default bank account created for user: Account Number: " + newId + ", Initial Balance: $0.00");

        return "User registered successfully. ID: " + newId + (isAdmin ? " (Admin)" : "");
    }

    public String login(String email, String pin) {
        int attempts = 0;
        final int MAX_ATTEMPTS = 3;

        while (attempts < MAX_ATTEMPTS) {
            if (!users.containsKey(email)) {
                return "Email not found.";
            }
            User user = users.get(email);
            if (!user.pin.equals(pin)) {
                attempts++;
                if (attempts == MAX_ATTEMPTS) {
                    return "Too many failed attempts. Account locked.";
                }
                return "Incorrect PIN. " + (MAX_ATTEMPTS - attempts) + " attempts remaining.";
            }
            return "Login successful. User ID: " + user.id + (user.isAdmin ? " (Admin)" : "");
        }
        return "Login failed.";
    }

    public String changePin(String email, String oldPin, String newPin) {
        if (!users.containsKey(email)) {
            return "User not found.";
        }
        User user = users.get(email);
        if (!user.pin.equals(oldPin)) {
            return "Incorrect old PIN.";
        }
        if (!Pattern.matches(PIN_REGEX, newPin)) {
            return "New PIN must be exactly 4 digits.";
        }
        user.pin = newPin;
        saveUserData();
        return "PIN changed successfully.";
    }

    public String addAdmin(String adminId, String name, String email, String number, String pin) {
        if (!users.containsKey(adminId) || !users.get(adminId).isAdmin) {
            return "Only admins can add new admins.";
        }
        return register(name, email, number, pin, true, adminId);
    }

    public String deleteAdmin(String adminId, String targetEmail) {
        if (!users.containsKey(adminId) || !users.get(adminId).isAdmin) {
            return "Only admins can delete admins.";
        }
        long adminCount = users.values().stream().filter(user -> user.isAdmin).count();
        if (adminCount <= 1) {
            return "Cannot delete the last admin.";
        }
        if (!users.containsKey(targetEmail) || !users.get(targetEmail).isAdmin) {
            return "Target is not an admin or does not exist.";
        }
        users.remove(targetEmail);
        saveUserData();
        return "Admin " + targetEmail + " deleted successfully.";
    }

    private void loadUserData() {
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_DATA_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 6) {
                    users.put(parts[2], new User(parts[0], parts[1], parts[2], parts[3], parts[4], Boolean.parseBoolean(parts[5])));
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("No previous user data found. Starting fresh.");
        } catch (IOException e) {
            System.out.println("Error loading user data: " + e.getMessage());
        }
    }

    private void saveUserData() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_DATA_FILE))) {
            for (User user : users.values()) {
                writer.write(String.format("%s:%s:%s:%s:%s:%b%n", user.id, user.name, user.email, user.number, user.pin, user.isAdmin));
            }
            System.out.println("User data saved successfully to " + USER_DATA_FILE);
        } catch (IOException e) {
            System.out.println("Error saving user data: " + e.getMessage());
        }
    }

    public boolean isValidUser(String userId) {
        return users.values().stream().anyMatch(user -> user.id.equals(userId));
    }

    public String getEmailByUserId(String userId) {
        for (User user : users.values()) {
            if (user.id.equals(userId)) {
                return user.email;
            }
        }
        return null;
    }

    public String getNumberByUserId(String userId) {
        for (User user : users.values()) {
            if (user.id.equals(userId)) {
                return user.number;
            }
        }
        return null;
    }

    public boolean isAdmin(String userId) {
        return users.values().stream().anyMatch(user -> user.id.equals(userId) && user.isAdmin);
    }
}