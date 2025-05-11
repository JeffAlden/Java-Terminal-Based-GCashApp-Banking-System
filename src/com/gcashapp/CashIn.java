package com.gcashapp;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class CashIn {
    private List<Transaction> transactions;
    private CheckBalance checkBalance;
    private static final String TRANSACTION_DATA_FILE = "C:\\Users\\javp1\\OneDrive\\Desktop\\GCashApp\\src\\com\\gcashapp\\transaction_data.txt";
    private static final String ACCOUNT_NUMBER_REGEX = "\\d{5}";

    public class Transaction {
        String id;
        double amount;
        String name;
        String accountID;
        String date;
        String transferToID;
        String transferFromID;
        private CheckBalance checkBalance;

        Transaction(String id, double amount, String name, String accountID, String date, String transferToID, String transferFromID, CheckBalance checkBalance) {
            this.id = id;
            this.amount = amount;
            this.name = name;
            this.accountID = accountID;
            this.date = date;
            this.transferToID = transferToID;
            this.transferFromID = transferFromID;
            this.checkBalance = checkBalance;
        }

        @Override
        public String toString() {
            if ("cash_in".equals(name)) {
                String userId = checkBalance.getUserIdByAccount(accountID);
                String fullName = checkBalance.getFullNameByUserId(userId);
                return String.format("[%s] Cash-In: PHP %.2f to Account %s (%s)", date, amount, accountID, fullName);
            } else if ("withdraw".equals(name)) {
                String userId = checkBalance.getUserIdByAccount(accountID);
                String fullName = checkBalance.getFullNameByUserId(userId);
                return String.format("[%s] Withdraw: PHP %.2f from Account %s (%s)", date, amount, accountID, fullName);
            } else if ("transfer".equals(name)) {
                String fromUserId = checkBalance.getUserIdByAccount(transferFromID);
                String toUserId = checkBalance.getUserIdByAccount(transferToID);
                String fromFullName = checkBalance.getFullNameByUserId(fromUserId);
                String toFullName = checkBalance.getFullNameByUserId(toUserId);
                return String.format("[%s] Transfer: PHP %.2f from Account %s (%s) to Account %s (%s)", date, amount, transferFromID, fromFullName, transferToID, toFullName);
            }
            return "";
        }
    }

    public CashIn(CheckBalance checkBalance) {
        this.checkBalance = checkBalance;
        transactions = new ArrayList<>();
        loadTransactionData();
    }

    public String cashIn(String accountNumber, double amount) {
        if (!accountNumber.matches(ACCOUNT_NUMBER_REGEX)) {
            return "Invalid account number format!";
        }
        if (!checkBalance.accountExists(accountNumber)) {
            return "Account not found.";
        }
        if (checkBalance.isAccountFrozen(accountNumber)) {
            return "Account is frozen.";
        }
        if (amount <= 0) {
            return "Amount must be positive.";
        }
        double currentBalance = checkBalance.getBalance(accountNumber); // Use getBalance method
        checkBalance.updateBalance(accountNumber, currentBalance + amount);
        String transactionId = String.valueOf(transactions.size() + 1);
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        transactions.add(new Transaction(transactionId, amount, "cash_in", accountNumber, date, null, null, checkBalance));
        saveTransactionData();
        return String.format("Cash-In successful: PHP %.2f to account %s.", amount, accountNumber);
    }

    public String withdraw(String accountNumber, double amount) {
        if (!accountNumber.matches(ACCOUNT_NUMBER_REGEX)) {
            return "Invalid account number format!";
        }
        if (!checkBalance.accountExists(accountNumber)) {
            return "Account not found.";
        }
        if (checkBalance.isAccountFrozen(accountNumber)) {
            return "Account is frozen.";
        }
        if (amount <= 0) {
            return "Amount must be positive.";
        }
        double currentBalance = checkBalance.getBalance(accountNumber); // Use getBalance method
        if (amount > currentBalance) {
            return "Insufficient funds in account.";
        }
        checkBalance.updateBalance(accountNumber, currentBalance - amount);
        String transactionId = String.valueOf(transactions.size() + 1);
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        transactions.add(new Transaction(transactionId, amount, "withdraw", accountNumber, date, null, null, checkBalance));
        saveTransactionData();
        return String.format("Withdrawal successful: PHP %.2f from account %s.", amount, accountNumber);
    }

    public String transfer(String fromAccount, String toAccount, double amount) {
        if (!fromAccount.matches(ACCOUNT_NUMBER_REGEX) || !toAccount.matches(ACCOUNT_NUMBER_REGEX)) {
            return "Invalid account number format!";
        }
        if (!checkBalance.accountExists(fromAccount) || !checkBalance.accountExists(toAccount)) {
            return "One or both accounts not found.";
        }
        if (checkBalance.isAccountFrozen(fromAccount) || checkBalance.isAccountFrozen(toAccount)) {
            return "One or both accounts are frozen.";
        }
        if (fromAccount.equals(toAccount)) {
            return "Source and destination accounts cannot be the same.";
        }
        if (amount <= 0) {
            return "Amount must be positive.";
        }
        double fromBalance = checkBalance.getBalance(fromAccount); // Use getBalance method
        if (amount > fromBalance) {
            return "Insufficient funds in source account.";
        }
        double toBalance = checkBalance.getBalance(toAccount); // Use getBalance method
        checkBalance.updateBalance(fromAccount, fromBalance - amount);
        checkBalance.updateBalance(toAccount, toBalance + amount);
        String transactionId = String.valueOf(transactions.size() + 1);
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        transactions.add(new Transaction(transactionId, amount, "transfer", null, date, toAccount, fromAccount, checkBalance));
        saveTransactionData();
        String fromUserId = checkBalance.getUserIdByAccount(fromAccount);
        String toUserId = checkBalance.getUserIdByAccount(toAccount);
        String fromFullName = checkBalance.getFullNameByUserId(fromUserId);
        String toFullName = checkBalance.getFullNameByUserId(toUserId);
        return String.format("Transferred PHP %.2f from Account %s (%s) to Account %s (%s).", amount, fromAccount, fromFullName, toAccount, toFullName);
    }

    public List<Transaction> getTransactionsForAccount(String accountId) {
        List<Transaction> accountTransactions = new ArrayList<>();
        for (Transaction t : transactions) {
            if (t.accountID != null && t.accountID.equals(accountId)) {
                accountTransactions.add(t);
            } else if (t.transferFromID != null && t.transferFromID.equals(accountId)) {
                accountTransactions.add(t);
            } else if (t.transferToID != null && t.transferToID.equals(accountId)) {
                accountTransactions.add(t);
            }
        }
        return accountTransactions;
    }

    public List<Transaction> getTransactionsForUser(String userId) {
        List<Transaction> userTransactions = new ArrayList<>();
        for (Transaction t : transactions) {
            String accountUserId = checkBalance.getUserIdByAccount(t.accountID != null ? t.accountID : 
                (t.transferFromID != null ? t.transferFromID : t.transferToID));
            if (accountUserId != null && accountUserId.equals(userId)) {
                userTransactions.add(t);
            }
        }
        return userTransactions;
    }

    public List<Transaction> getAllTransactions() {
        return new ArrayList<>(transactions);
    }

    private void loadTransactionData() {
        try (BufferedReader reader = new BufferedReader(new FileReader(TRANSACTION_DATA_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 7) {
                    transactions.add(new Transaction(parts[0], Double.parseDouble(parts[1]), parts[2], parts[3].isEmpty() ? null : parts[3], 
                        parts[4], parts[5].isEmpty() ? null : parts[5], parts[6].isEmpty() ? null : parts[6], checkBalance));
                }
            }
            System.out.println("Transaction data loaded successfully from " + TRANSACTION_DATA_FILE);
        } catch (FileNotFoundException e) {
            System.out.println("No previous transaction data found. Starting fresh.");
        } catch (IOException e) {
            System.out.println("Error loading transaction data: " + e.getMessage());
        }
    }

    private void saveTransactionData() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TRANSACTION_DATA_FILE))) {
            for (Transaction t : transactions) {
                String accountID = t.accountID != null ? t.accountID : "";
                String transferToID = t.transferToID != null ? t.transferToID : "";
                String transferFromID = t.transferFromID != null ? t.transferFromID : "";
                writer.write(String.format("%s:%.2f:%s:%s:%s:%s:%s%n", t.id, t.amount, t.name, accountID, t.date, transferToID, transferFromID));
            }
            System.out.println("Transaction data saved successfully to " + TRANSACTION_DATA_FILE);
        } catch (IOException e) {
            System.out.println("Error saving transaction data: " + e.getMessage());
        }
    }
}