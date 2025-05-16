import java.util.List;
import java.util.Scanner;

public class IOinterface {
    private static IOinterface instance;
    private Scanner scanner = new Scanner(System.in);

    private IOinterface() {}

    public static IOinterface getInstance() {
        if (instance == null) {
            instance = new IOinterface();
        }
        return instance;
    }

    public String[] getUserInput(String message, int numOfArgs) {
        System.out.println(message);
        String[] input = scanner.nextLine().split(" ");
        String[] result = new String[numOfArgs];
        for (int i = 0; i < numOfArgs; i++) {
            result[i] = i < input.length ? input[i] : "";
        }
        return result;
    }

    public void mainMenu() {
        System.out.println("(1)) Login\n(2)Register\n(3))Quit");
    }

    public void adminMenu() {
        System.out.println("(1) Show products\n(2) Add customers\n(3) Show customers\n(4) Show orders\n (5) Generate test data\n (6) Generate statistics\n (7) Delete all data\n (8) Logout");
    }

    public void customerMenu() {
        System.out.println("(1)Show profile\n(2) Update profile\n(3) Show products\n (4)Show orders\n (5). Generate figures\n(6). Logout");
   
    }

    public void showList(String listType, List<?> items) {
        System.out.println(listType + " List:");
        for (int i = 0; i < items.size(); i++) {
            System.out.println((i + 1) + ". " + items.get(i));
        }
    }

    public void printErrorMessage(String source, String message) {
        System.out.println("Error in " + source + ": " + message);
    }

    public void printMessage(String message) {
        System.out.println(message);
    }

    public void printObject(Object object) {
        System.out.println(object);
    }
}