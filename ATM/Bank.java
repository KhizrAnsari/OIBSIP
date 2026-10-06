import java.util.ArrayList;

public class Bank {

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