import java.util.List;
import java.util.Scanner;

public class IOinterface {
    private static IOinterface instance;
    private Scanner scanner = new Scanner(System.in);

    private IOinterface() {}

    public static IOinterface getInstance() {
        if (instance == null) {  instance = new IOinterface(); }
        return instance;
    }

    public String[] getUserInput(String message, int numOfArgs) {
        System.out.print(message + ": ");
        String input = scanner.nextLine().trim();
        String[] args = input.split(" ");
        String[] result = new String[numOfArgs];
        for (int i = 0; i < numOfArgs; i++) {
            result[i] = (i < args.length) ? args[i] : ""; 
        }
        return result;
    }


    public void mainMenu() {
        System.out.println("====== E-Commerce System ======\n");
        System.out.println("1. Login\n2. Register\n3. Quit\n");
        System.out.println("===========================\n");
    }

    public void adminMenu() {
        System.out.println("====== Admin Menu ======\n");
        System.out.println("1. Show products\n2. Add customers\n3. Show customers\n4. Show orders\n5. Generate test data\n6. Generate all statistical figures\n7. Delete all data\n8. Logout\n");
        System.out.println("====================\n");
    }

    public void customerMenu() {
        System.out.println("====== Customer Menu ======\n");
        System.out.println("1. Show profile\n2. Update profile\n3. Show products\n4. Show history orders\n5. Generate all consumption figures\n6. Logout\n");
        System.out.println("====================\n");
    }

    public void showList(String userRole, String listType, List<?> objectList, int pageNumber, int totalPages) {
        System.out.println("\n====== " + listType + " List ======");
        System.out.println("Role: " + userRole);
        System.out.println("Page " + pageNumber + " of " + totalPages);
        System.out.println("==================================");

        if (objectList.isEmpty()) {
            System.out.println("No " + listType.toLowerCase() + " found.");
            return;
        }

        int rowNum = (pageNumber - 1) * 10 + 1;
        for (Object obj : objectList) {
            System.out.println(rowNum++ + ". " + obj.toString());
        }

        System.out.println("==================================");
    }

    public void printErrorMessage(String errorSource, String errorMessage) {
        System.out.println("Error in " + errorSource + ": " + errorMessage);
    }

    public void printMessage(String message) {
        System.out.println(message);
    }

    public void printObject(Object targetObject) {
        System.out.println(targetObject);
    }
}