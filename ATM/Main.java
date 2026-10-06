import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

// ===================== MAIN =====================
public class Main {
    public static void main(String[] args) {
        Bank bank = new Bank();
        ATM atm = new ATM(bank);
        atm.start();
    }
}

// ===================== TRANSACTION =====================
class Transaction {

    private String type;
    private double amount;
    private double balanceAfter;
    private LocalDateTime dateTime;

    public Transaction(String type, double amount, double balanceAfter) {
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.dateTime = LocalDateTime.now();
    }

    public void display() {
        DateTimeFormatter f = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        System.out.println(type + " | Amount: Rs. " + amount
                + " | Balance: Rs. " + balanceAfter
                + " | " + dateTime.format(f));
    }
}

// ===================== ACCOUNT =====================
class Account {

    private String userId;
    private String pin;
    private double balance;
    private ArrayList<Transaction> transactions = new ArrayList<Transaction>();

    public Account(String userId, String pin, double balance) {
        this.userId = userId;
        this.pin = pin;
        this.balance = balance;
    }

    public String getUserId() {
        return userId;
    }

    public double getBalance() {
        return balance;
    }

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    public boolean checkPin(String enteredPin) {
        return pin.equals(enteredPin);
    }

    public boolean deposit(double amount) {
        return deposit(amount, "Deposit");
    }

    public boolean deposit(double amount, String label) {
        if (amount <= 0) {
            return false;
        }
        balance = balance + amount;
        transactions.add(new Transaction(label, amount, balance));
        return true;
    }

    public boolean withdraw(double amount) {
        return withdraw(amount, "Withdraw");
    }

    public boolean withdraw(double amount, String label) {
        if (amount <= 0 || amount > balance) {
            return false;
        }
        balance = balance - amount;
        transactions.add(new Transaction(label, amount, balance));
        return true;
    }
}

// ===================== BANK =====================
class Bank {

    private ArrayList<Account> accounts = new ArrayList<Account>();

    public Bank() {
        accounts.add(new Account("1001", "1234", 10000));
        accounts.add(new Account("1002", "4321", 5000));
    }

    public Account findAccount(String userId) {
        for (Account acc : accounts) {
            if (acc.getUserId().equals(userId)) {
                return acc;
            }
        }
        return null;
    }
}

// ===================== ATM =====================
class ATM {

    private Bank bank;
    private Scanner sc = new Scanner(System.in);

    public ATM(Bank bank) {
        this.bank = bank;
    }

    public void start() {
        System.out.println("================================");
        System.out.println("        ATM INTERFACE");
        System.out.println("================================");

        Account account = login();

        if (account == null) {
            System.out.println("\nToo many wrong attempts. Session ended.");
            return;
        }

        System.out.println("\nLogin Successful!");
        showMenu(account);
    }

    private Account login() {
        for (int attempt = 1; attempt <= 3; attempt++) {
            System.out.print("\nEnter User ID: ");
            String id = sc.nextLine();
            System.out.print("Enter PIN: ");
            String pin = sc.nextLine();

            Account acc = bank.findAccount(id);
            if (acc != null && acc.checkPin(pin)) {
                return acc;
            }
            System.out.println("Invalid User ID or PIN! Attempts left: " + (3 - attempt));
        }
        return null;
    }

    private void showMenu(Account account) {
        while (true) {
            System.out.println("\n========== ATM MENU ==========");
            System.out.println("1. Check Balance");
            System.out.println("2. Withdraw");
            System.out.println("3. Deposit");
            System.out.println("4. Transfer");
            System.out.println("5. Transaction History");
            System.out.println("6. Logout");
            System.out.print("Enter your choice: ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    checkBalance(account);
                    break;
                case "2":
                    withdraw(account);
                    break;
                case "3":
                    deposit(account);
                    break;
                case "4":
                    transfer(account);
                    break;
                case "5":
                    showHistory(account);
                    break;
                case "6":
                    System.out.println("Logged out. Thank you!");
                    return;
                default:
                    System.out.println("Invalid choice! Select 1 to 6.");
            }
        }
    }

    // Amount lene ka helper. Galat input par -1 return karta hai
    private double readAmount() {
        try {
            double amount = Double.parseDouble(sc.nextLine().trim());
            if (amount <= 0) {
                System.out.println("Amount must be greater than zero.");
                return -1;
            }
            return amount;
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount!");
            return -1;
        }
    }

    private void checkBalance(Account account) {
        System.out.println("Current Balance: Rs. " + account.getBalance());
    }

    private void withdraw(Account account) {
        System.out.print("Enter amount to withdraw: Rs. ");
        double amount = readAmount();
        if (amount < 0) {
            return;
        }

        if (account.withdraw(amount)) {
            System.out.println("Withdrawal Successful! Balance: Rs. " + account.getBalance());
        } else {
            System.out.println("Insufficient balance!");
        }
    }

    private void deposit(Account account) {
        System.out.print("Enter amount to deposit: Rs. ");
        double amount = readAmount();
        if (amount < 0) {
            return;
        }

        account.deposit(amount);
        System.out.println("Deposit Successful! Balance: Rs. " + account.getBalance());
    }

    private void transfer(Account sender) {
        System.out.print("Enter receiver User ID: ");
        Account receiver = bank.findAccount(sc.nextLine().trim());

        if (receiver == null) {
            System.out.println("Receiver account not found.");
            return;
        }
        if (receiver.getUserId().equals(sender.getUserId())) {
            System.out.println("You cannot transfer to yourself.");
            return;
        }

        System.out.print("Enter amount to transfer: Rs. ");
        double amount = readAmount();
        if (amount < 0) {
            return;
        }

        if (sender.withdraw(amount, "Transfer to " + receiver.getUserId())) {
            receiver.deposit(amount, "Received from " + sender.getUserId());
            System.out.println("Transfer Successful! Balance: Rs. " + sender.getBalance());
        } else {
            System.out.println("Insufficient balance!");
        }
    }

    private void showHistory(Account account) {
        System.out.println("\n===== TRANSACTION HISTORY =====");
        if (account.getTransactions().isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }
        for (Transaction t : account.getTransactions()) {
            t.display();
        }
    }
}