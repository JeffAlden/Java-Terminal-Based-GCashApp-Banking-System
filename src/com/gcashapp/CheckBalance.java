package com.gcashapp;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class CheckBalance {
    private Map<String, Double> accountBalances;
    private Map<String, String> accountUserMap;
    private Map<String, Boolean> accountFrozenMap;
    private Map<String, String> userNameMap;
    private static final String ACCOUNT_DATA_FILE = "C:\\Users\\javp1\\OneDrive\\Desktop\\GCashApp\\src\\com\\gcashapp\\account_data.txt";
    private static final String USER_DATA_FILE = "C:\\Users\\javp1\\OneDrive\\Desktop\\GCashApp\\src\\com\\gcashapp\\user_data.txt";

    public CheckBalance() {
        accountBalances = new HashMap<>();
        accountUserMap = new HashMap<>();
        accountFrozenMap = new HashMap<>();
        userNameMap = new HashMap<>();
        loadAccountData();
        loadUserData();
    }

    private void loadAccountData() {
        try (BufferedReader reader = new BufferedReader(new FileReader(ACCOUNT_DATA_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 4) { // accountNumber:userId:balance:isFrozen
                    String accountNumber = parts[0];
                    String userId = parts[1];
                    double balance = Double.parseDouble(parts[2]); // Balance is now parts[2]
                    boolean isFrozen = Boolean.parseBoolean(parts[3]);
                    accountBalances.put(accountNumber, balance);
                    accountUserMap.put(accountNumber, userId);
                    accountFrozenMap.put(accountNumber, isFrozen);
                }
            }
            System.out.println("Account data loaded successfully from " + ACCOUNT_DATA_FILE);
        } catch (IOException e) {
            System.out.println("Error loading account data: " + e.getMessage());
        }
    }

    private void loadUserData() {
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_DATA_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length >= 2) {
                    userNameMap.put(parts[0], parts[1]);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading user data: " + e.getMessage());
        }
    }

    public double checkBalance(String userId) {
        double totalBalance = 0.0;
        for (Map.Entry<String, Double> entry : accountBalances.entrySet()) {
            if (accountUserMap.get(entry.getKey()).equals(userId)) {
                totalBalance += entry.getValue();
            }
        }
        return totalBalance;
    }

    public boolean accountExists(String accountNumber) {
        return accountBalances.containsKey(accountNumber);
    }

    public String getUserIdByAccount(String accountNumber) {
        return accountUserMap.getOrDefault(accountNumber, null);
    }

    public String getFullNameByUserId(String userId) {
        return userNameMap.getOrDefault(userId, "Unknown User");
    }

    public void updateBalance(String accountNumber, double newBalance) {
        accountBalances.put(accountNumber, newBalance);
        saveAccountData();
    }

    public double getBalance(String accountNumber) {
        return accountBalances.getOrDefault(accountNumber, 0.0);
    }

    public boolean isAccountFrozen(String accountNumber) {
        return accountFrozenMap.getOrDefault(accountNumber, false);
    }

    public void freezeAccount(String accountNumber, boolean freeze) {
        accountFrozenMap.put(accountNumber, freeze);
        saveAccountData();
    }

    public void deleteAccount(String accountNumber) {
        accountBalances.remove(accountNumber);
        accountUserMap.remove(accountNumber);
        accountFrozenMap.remove(accountNumber);
        saveAccountData();
    }

    public void addAccount(String accountNumber, double initialBalance, String userId) {
        accountBalances.put(accountNumber, initialBalance);
        accountUserMap.put(accountNumber, userId);
        accountFrozenMap.put(accountNumber, false);
        saveAccountData();
    }

    public void forceSaveAccountData() {
        saveAccountData();
    }

    private void saveAccountData() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ACCOUNT_DATA_FILE))) {
            for (Map.Entry<String, Double> entry : accountBalances.entrySet()) {
                String accountNumber = entry.getKey();
                String userId = accountUserMap.get(accountNumber);
                boolean isFrozen = accountFrozenMap.getOrDefault(accountNumber, false);
                writer.write(String.format("%s:%s:%.2f:%b%n", accountNumber, userId, entry.getValue(), isFrozen)); // Match file format
            }
            System.out.println("Account data saved successfully to " + ACCOUNT_DATA_FILE);
        } catch (IOException e) {
            System.out.println("Error saving account data: " + e.getMessage());
        }
    }
}