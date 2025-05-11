package com.gcashapp;

import java.util.*;

public class BankingApp {
    private UserAuthentication auth;
    private CheckBalance checkBalance;
    private CashIn cashIn;
    private Scanner scanner;
    private String loggedInUserId = null;
    private static final String ACCOUNT_NUMBER_REGEX = "\\d{5}";

    public BankingApp() {
        scanner = new Scanner(System.in);
        checkBalance = new CheckBalance();
        cashIn = new CashIn(checkBalance);
        auth = new UserAuthentication(checkBalance, cashIn);
    }

    public void start() {
        while (true) {
            if (loggedInUserId == null) {
                displayDefaultMenu();
                int choice = getUserChoice();
                if (choice == 7) {
                    System.out.println("Thank you for using GCashApp. Goodbye!");
                    break;
                }
                processDefaultChoice(choice);
            } else {
                boolean continueTransactions = true;
                while (continueTransactions) {
                    if (loggedInUserId.equals("10001")) {
                        displayAdminMenu();
                        int choice = getUserChoice();
                        if (choice == 10) {
                            loggedInUserId = null;
                            System.out.println("Logged out successfully.");
                            break; // Exit inner loop for admin
                        }
                        continueTransactions = processAdminChoice(choice);
                    } else {
                        displayUserMenu();
                        int choice = getUserChoice();
                        if (choice == 7) { // Logout for regular user
                            loggedInUserId = null;
                            System.out.println("Thank you for using GCashApp. Goodbye!");
                            return; // Exit the entire method, terminating the app
                        }
                        continueTransactions = processUserChoice(choice);
                    }
                }
            }
        }
        scanner.close();
    }

    private void displayDefaultMenu() {
        System.out.println("\n=== GCashApp User Interface ===");
        System.out.println("1. Login");
        System.out.println("2. Create Account");
        System.out.println("3. Check Balance");
        System.out.println("4. Deposit");
        System.out.println("5. Withdraw");
        System.out.println("6. Transfer Funds");
        System.out.println("7. Exit");
        System.out.print("Enter your choice: ");
    }

    private void displayUserMenu() {
        System.out.println("\n=== GCashApp User Interface ===");
        System.out.println("1. Check Balance");
        System.out.println("2. Deposit");
        System.out.println("3. Withdraw");
        System.out.println("4. Transfer Funds");
        System.out.println("5. View Transaction History");
        System.out.println("6. Change PIN");
        System.out.println("7. Logout");
        System.out.print("Enter your choice: ");
    }

    private void displayAdminMenu() {
        System.out.println("\n=== GCashApp Admin Interface ===");
        System.out.println("1. Check Balance");
        System.out.println("2. Deposit");
        System.out.println("3. Withdraw");
        System.out.println("4. Transfer Funds");
        System.out.println("5. View Transaction History");
        System.out.println("6. Freeze Account");
        System.out.println("7. Delete Account");
        System.out.println("8. Add Admin");
        System.out.println("9. Delete Admin");
        System.out.println("10. Logout");
        System.out.print("Enter your choice: ");
    }

    private int getUserChoice() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void processDefaultChoice(int choice) {
        switch (choice) {
            case 1:
                loginUser();
                break;
            case 2:
                registerUser();
                break;
            case 3:
                loginAndCheckBalance();
                break;
            case 4:
                loginAndDeposit();
                break;
            case 5:
                loginAndWithdraw();
                break;
            case 6:
                loginAndTransferFunds();
                break;
            case 7:
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }

    private boolean processUserChoice(int choice) {
        switch (choice) {
            case 1:
                checkBalance();
                break;
            case 2:
                deposit();
                break;
            case 3:
                withdraw();
                break;
            case 4:
                transferFunds();
                break;
            case 5:
                viewTransactionHistory();
                break;
            case 6:
                changePin();
                break;
            case 7:
                return false; // No change needed here, handled in start method
            default:
                System.out.println("Invalid choice. Please try again.");
        }
        System.out.print("Do you want to perform another transaction? (yes/no): ");
        return scanner.nextLine().trim().equalsIgnoreCase("yes");
    }

    private boolean processAdminChoice(int choice) {
        switch (choice) {
            case 1:
                checkBalance();
                break;
            case 2:
                deposit();
                break;
            case 3:
                withdraw();
                break;
            case 4:
                transferFunds();
                break;
            case 5:
                viewTransactionHistory();
                break;
            case 6:
                freezeAccount();
                break;
            case 7:
                deleteAccount();
                break;
            case 8:
                addAdmin();
                break;
            case 9:
                deleteAdmin();
                break;
            case 10:
                return false;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
        System.out.print("Do you want to perform another transaction? (yes/no): ");
        return scanner.nextLine().trim().equalsIgnoreCase("yes");
    }

    private void registerUser() {
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();
        System.out.print("Enter your email: ");
        String email = scanner.nextLine();
        System.out.print("Enter your 10-digit phone number: ");
        String number = scanner.nextLine();
        System.out.print("Enter your 4-digit PIN: ");
        String pin = scanner.nextLine();
        String result = auth.register(name, email, number, pin, false, null);
        System.out.println(result);
    }

    private boolean loginUser() {
        System.out.print("Enter your email: ");
        String email = scanner.nextLine();
        System.out.print("Enter your 4-digit PIN: ");
        String pin = scanner.nextLine();
        String result = auth.login(email, pin);
        System.out.println(result);
        if (result.startsWith("Login successful")) {
            loggedInUserId = result.split(": ")[1].split(" ")[0];
            return true;
        }
        return false;
    }

    private void loginAndCheckBalance() {
        if (loginUser()) {
            checkBalance();
            loggedInUserId = null;
        }
    }

    private void loginAndDeposit() {
        if (loginUser()) {
            deposit();
            loggedInUserId = null;
        }
    }

    private void loginAndWithdraw() {
        if (loginUser()) {
            withdraw();
            loggedInUserId = null;
        }
    }

    private void loginAndTransferFunds() {
        if (loginUser()) {
            transferFunds();
            loggedInUserId = null;
        }
    }

    private void changePin() {
        System.out.print("Enter your email: ");
        String email = scanner.nextLine();
        System.out.print("Enter your old PIN: ");
        String oldPin = scanner.nextLine();
        System.out.print("Enter your new 4-digit PIN: ");
        String newPin = scanner.nextLine();
        System.out.println(auth.changePin(email, oldPin, newPin));
    }

    private boolean isValidAccountNumber(String accountNumber) {
        return accountNumber.matches(ACCOUNT_NUMBER_REGEX);
    }

    private void checkBalance() {
        double balance = checkBalance.checkBalance(loggedInUserId);
        System.out.printf("Total balance for user: PHP %.2f%n", balance);
    }

    private void deposit() {
        System.out.print("Enter 5-digit account number: ");
        String accountNumber = scanner.nextLine();
        if (!isValidAccountNumber(accountNumber)) {
            System.out.println("Invalid account number format!");
            return;
        }
        System.out.println("Debug: Checking if account exists: " + checkBalance.accountExists(accountNumber));
        System.out.println("Debug: Account user ID: " + checkBalance.getUserIdByAccount(accountNumber));
        System.out.println("Debug: Logged-in user ID: " + loggedInUserId);
        if (!checkBalance.accountExists(accountNumber) || !checkBalance.getUserIdByAccount(accountNumber).equals(loggedInUserId)) {
            System.out.println("Account not found or access denied!");
            return;
        }
        System.out.print("Enter amount to deposit: ");
        try {
            double amount = Double.parseDouble(scanner.nextLine());
            System.out.println(cashIn.cashIn(accountNumber, amount));
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount format!");
        }
    }

    private void withdraw() {
        System.out.print("Enter 5-digit account number: ");
        String accountNumber = scanner.nextLine();
        if (!isValidAccountNumber(accountNumber)) {
            System.out.println("Invalid account number format!");
            return;
        }
        if (!checkBalance.accountExists(accountNumber) || !checkBalance.getUserIdByAccount(accountNumber).equals(loggedInUserId)) {
            System.out.println("Account not found or access denied!");
            return;
        }
        System.out.print("Enter amount to withdraw: ");
        try {
            double amount = Double.parseDouble(scanner.nextLine());
            System.out.println(cashIn.withdraw(accountNumber, amount));
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount format!");
        }
    }

    private void transferFunds() {
        System.out.print("Enter source 5-digit account number: ");
        String fromAccount = scanner.nextLine();
        if (!isValidAccountNumber(fromAccount)) {
            System.out.println("Invalid source account number format!");
            return;
        }
        if (!checkBalance.accountExists(fromAccount) || !checkBalance.getUserIdByAccount(fromAccount).equals(loggedInUserId)) {
            System.out.println("Source account not found or access denied!");
            return;
        }
        System.out.print("Enter destination 5-digit account number: ");
        String toAccount = scanner.nextLine();
        if (!isValidAccountNumber(toAccount)) {
            System.out.println("Invalid destination account number format!");
            return;
        }
        if (!checkBalance.accountExists(toAccount)) {
            System.out.println("Destination account not found!");
            return;
        }
        System.out.print("Enter amount to transfer: ");
        try {
            double amount = Double.parseDouble(scanner.nextLine());
            System.out.println(cashIn.transfer(fromAccount, toAccount, amount));
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount format!");
        }
    }

    private void viewTransactionHistory() {
        List<CashIn.Transaction> userTransactions = cashIn.getTransactionsForUser(loggedInUserId);
        if (userTransactions.isEmpty()) {
            System.out.println("No transactions found for this user.");
        } else {
            System.out.println("\nTransaction History for User ID " + loggedInUserId + ":");
            for (CashIn.Transaction t : userTransactions) {
                System.out.println(t);
            }
        }
    }

    private void freezeAccount() {
        System.out.print("Enter 5-digit account number to freeze/unfreeze: ");
        String accountNumber = scanner.nextLine();
        if (!isValidAccountNumber(accountNumber)) {
            System.out.println("Invalid account number format!");
            return;
        }
        if (!checkBalance.accountExists(accountNumber)) {
            System.out.println("Account not found!");
            return;
        }
        boolean isFrozen = checkBalance.isAccountFrozen(accountNumber);
        checkBalance.freezeAccount(accountNumber, !isFrozen);
        System.out.println("Account " + accountNumber + " is now " + (!isFrozen ? "frozen" : "unfrozen") + ".");
    }

    private void deleteAccount() {
        System.out.print("Enter 5-digit account number to delete: ");
        String accountNumber = scanner.nextLine();
        if (!isValidAccountNumber(accountNumber)) {
            System.out.println("Invalid account number format!");
            return;
        }
        if (!checkBalance.accountExists(accountNumber)) {
            System.out.println("Account not found!");
            return;
        }
        checkBalance.deleteAccount(accountNumber);
        System.out.println("Account " + accountNumber + " deleted successfully.");
    }

    private void addAdmin() {
        System.out.print("Enter admin name: ");
        String name = scanner.nextLine();
        System.out.print("Enter admin email: ");
        String email = scanner.nextLine();
        System.out.print("Enter 10-digit phone number: ");
        String number = scanner.nextLine();
        System.out.print("Enter 4-digit PIN: ");
        String pin = scanner.nextLine();
        System.out.println(auth.addAdmin(loggedInUserId, name, email, number, pin));
    }

    private void deleteAdmin() {
        System.out.print("Enter email of admin to delete: ");
        String targetEmail = scanner.nextLine();
        System.out.println(auth.deleteAdmin(loggedInUserId, targetEmail));
    }

    public static void main(String[] args) {
        BankingApp app = new BankingApp();
        app.start();
    }
}