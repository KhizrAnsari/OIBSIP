import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction {

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