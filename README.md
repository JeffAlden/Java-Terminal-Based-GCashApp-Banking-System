echo # Java Terminal-Based GCashApp Banking System > README.md
echo. >> README.md
echo A Java-based terminal application simulating a banking system, featuring user registration, account management, and transaction functionalities such as deposits, withdrawals, and transfers. This project demonstrates core Java programming concepts including OOP, file I/O, collections, and exception handling. >> README.md
echo. >> README.md
echo ## Setup and Installation >> README.md
echo 1. Clone the repository: `git clone https://github.com/JeffAlden/Java-Terminal-Based-GCashApp-Banking-System.git` >> README.md
echo 2. Navigate to `src/` and compile: `javac com/gcashapp/*.java` >> README.md
echo 3. Run: `java com.gcashapp.BankingApp` >> README.md
echo. >> README.md
echo ## Creating Required Data Files >> README.md
echo The application uses three text files for data storage, which are not included (as per `.gitignore`). Create them in `src/com/gcashapp/`: >> README.md
echo - `user_data.txt`: `10001:Admin:admin@gmail.com:09123456789:1234:true` >> README.md
echo - `account_data.txt`: `10001:10001:1000.00:false` >> README.md
echo - `transaction_data.txt`: (can be empty initially) >> README.md

GCashApp
├── .git/
├── src/
│ └── com/
│ └── gcashapp/
│ ├── BankingApp.java
│ ├── CheckBalance.java
│ ├── CashIn.java
│ └── UserAuthentication.java
│ ├── user_data.txt (local only, not tracked)
│ ├── account_data.txt (local only, not tracked)
│ └── transaction_data.txt (local only, not tracked)
├── .gitignore
├── README.md
├── LICENSE
