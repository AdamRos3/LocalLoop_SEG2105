
//import java.security.SecurityPermission;
import java.util.Scanner;

public class ProgramManager {
    //Scanner Setup = new Scanner(System.in);
    //ArrayList<UserAccount> userAccounts;

    public ProgramManager() {
        //ArrayList<UserAccount> userAccounts = new ArrayList<UserAccount>();
        //Scanner Setup = new Scanner(System.in);
    }

    public static void participantSetup() {
        Scanner participantSetup = new Scanner(System.in);
        System.out.println("Hello, Participant. ");
        System.out.println("Participant, what is your username for account creation... ");
        String inputtedUsername = participantSetup.nextLine();
        System.out.println("Participant, what is your password for account creation... ");
        String inputtedPassword = participantSetup.nextLine();
        Participant createdParticipant = new Participant(inputtedUsername, inputtedPassword);
        createdParticipant.welcome();
        createdParticipant.addUserAccount();
    }

    public static void organizerSetup() {
        Scanner organizerSetup = new Scanner(System.in);
        System.out.println("Hello, Organizer. ");
        System.out.println("Organizer, what is your username for account creation... ");
        String inputtedUsername = organizerSetup.nextLine();
        System.out.println("Organizer, what is your password for account creation... ");
        String inputtedPassword = organizerSetup.nextLine();
        Organizer createdOrganizer = new Organizer(inputtedUsername, inputtedPassword);
        createdOrganizer.welcome();
        createdOrganizer.addUserAccount();
        //return createdOrganizer;
        //userAccounts.add(createdOrganizer);
    }

    public static void adminSetup() {
        Admin createdAdmin = new Admin();
        createdAdmin.addUserAccount();
        createdAdmin.welcome();
    }

    public static void main(String[] args) {
        Scanner Setup = new Scanner(System.in);
        System.out.println("Welcome to LocalLoop! Please enter your username below. (If you don't yet have an account please enter \"create\")");
        String inputtedString = Setup.nextLine();
        inputtedString = inputtedString.toLowerCase();
        switch (inputtedString) {
            case "participant":
                System.out.println("Testing: partipant creation called... ");
                participantSetup();
                break;
                //userAccounts.add(organizerSetup());
            case "organizer":
                System.out.println("Testing: organizer creation called... ");
                organizerSetup();
                break;
            case "admin":
                System.out.println("Testing: admin called... ");
                adminSetup();
                break;
        }
    }
}
