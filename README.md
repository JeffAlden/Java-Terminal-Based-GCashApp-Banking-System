
# Java Terminal-Based GCashApp Banking System

A Java-based terminal application simulating a banking system, featuring user registration, account management, and transaction functionalities such as deposits, withdrawals, and transfers. This project demonstrates core Java programming concepts including OOP, file I/O, collections, and exception handling.

## Setup and Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/JeffAlden/Java-Terminal-Based-GCashApp-Banking-System.git
   ```
2. Navigate to `src/` and compile:
   ```bash
   javac com/gcashapp/*.java
   ```
3. Run the application:
   ```bash
   java com.gcashapp.BankingApp
   ```

## Creating Required Data Files

The application uses three text files for data storage, which are not included (as per .gitignore). Create them in `src/com/gcashapp/`:

- `user_data.txt`:  
   ```
   10001:Admin:admin@gmail.com:09123456789:1234:true
   ```
- `account_data.txt`:  
   ```
   10001:10001:1000.00:false
   ```
- `transaction_data.txt`: (can be empty initially)

## Project Structure

```
GCashApp
├── .git/
├── src/
│   └── com/
│       └── gcashapp/
│           ├── BankingApp.java
│           ├── CheckBalance.java
│           ├── CashIn.java
│           └── UserAuthentication.java
│           ├── user_data.txt (local only, not tracked)
│           ├── account_data.txt (local only, not tracked)
│           └── transaction_data.txt (local only, not tracked)
├── .gitignore
├── README.md
├── LICENSE
```
