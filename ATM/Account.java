import java.util.ArrayList;

public class Account {

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