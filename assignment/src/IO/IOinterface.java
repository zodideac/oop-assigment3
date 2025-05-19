package IO;
import java.util.List;
import java.util.Scanner;

public class IOinterface {
    private static IOinterface instance;
    private Scanner scanner;

    private IOinterface() {
        scanner = new Scanner(System.in);
    }

    public static IOinterface getInstance() {
        if (instance == null) { instance = new IOinterface(); }
        return instance;    
    }    
        
    
    public String[] getUserInput(String message, int numOfArgs) {
        System.out.println(message);
        String input = scanner.nextLine();
        String[] parts = input.trim().split("\\s+");
        String[] result = new String[numOfArgs];
        
        for (int i = 0; i < numOfArgs; i++) {
            result[i] = (i < parts.length) ? parts[i] : "";
        }
        
        return result;
    }

    public void mainMenu() {
        System.out.println("====== E-Commerce System ======\n");
        System.out.println("1. Login\n");
        System.out.println("2. Register\n");
        System.out.println("3. Quit\n");
        System.out.println("===========================");
    }

    public void adminMenu() {
        System.out.println("====== Admin Menu ======\n");
        System.out.println("1. Show products\n");
        System.out.println("2. Add customers\n");
        System.out.println("3. Show customers\n");
        System.out.println("4. Show orders\n");
        System.out.println("5. Generate test data\n");
        System.out.println("6. Generate all statistical figures\n");
        System.out.println("7. Delete all data\n");
        System.out.println("8. Logout\n");
        System.out.println("=====================");
    }

    public void customerMenu() {
        System.out.println("====== Customer Menu ======\n");
        System.out.println("1. Show profile\n");
        System.out.println("2. Update profile\n");
        System.out.println("3. Show products (or search using a keyword)\n");
        System.out.println("4. Show history orders\n");
        System.out.println("5. Generate all consumption figures\n");
        System.out.println("6. Logout\n");
        System.out.println("=======================");
    }

    public void showList(String userRole, String listType, List<?> objectList, int pageNumber, int totalPages) {
        System.out.println("==== " + listType + " List ====");
        System.out.println("Role: " + userRole);
        System.out.println("Page " + pageNumber + " of " + totalPages);
        int count = 1;
        for (Object obj : objectList) {
            System.out.println(count + ". " + obj.toString());
            count++;
        }
    }

    public void printErrorMessage(String errorSource, String errorMessage) {
        System.out.println("[Error] " + errorSource + ": " + errorMessage);
    }

    public void printMessage(String message) {
        System.out.println(message);
    }

    public void printObject(Object targetObject) {
        System.out.println(targetObject.toString());
    }
}