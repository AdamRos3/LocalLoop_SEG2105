
import java.util.ArrayList;

public class UserAccount {
    public String username;
    public String password;
    public String typeOfAccount;
    private static ArrayList<UserAccount> userAccounts = new ArrayList<UserAccount>();
    //static ArrayList<UserAccount> userAccounts = new ArrayList<UserAccount>();
    public UserAccount(String inputtedUsername, String inputtedPassword) {
        username = inputtedUsername;
        password = inputtedPassword;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void addUserAccount() {
        userAccounts.add(this);
    }

    public String toString() {
        return typeOfAccount + ", Username: " + this.getUsername() + ", Password: " + this.getPassword();
    }

    public void welcome() {
        System.out.println("Welcome " + username + "! You are logged in as \"" + typeOfAccount + "\"");
    }

    public ArrayList<UserAccount> getUserAccounts() {
        return userAccounts;
    }
}
