package IO;

import java.util.List;
import java.util.Scanner;

/**
 * The {@code IOinterface} class provides a simple interface for console input/output
 * operations within the e-commerce system.
 * <p>
 * This class is implemented as a singleton to ensure that only one instance manages console input.
 * It offers methods for fetching user input, displaying various menus, printing messages,
 * and displaying lists of objects.
 * </p>
 */
public class IOinterface {

    /** Singleton instance of IOinterface. */
    private static IOinterface instance;
    
    /** Scanner for reading console input. */
    private Scanner scanner;

    /**
     * Private constructor that initializes the Scanner for System.in.
     */
    private IOinterface() {
        scanner = new Scanner(System.in);
    }

    /**
     * Retrieves the singleton instance of {@code IOinterface}. If the instance does not exist,
     * it is created.
     *
     * @return the singleton instance of IOinterface.
     */
    public static IOinterface getInstance() {
        if (instance == null) {
            instance = new IOinterface();
        }
        return instance;
    }

    /**
     * Prompts the user with a message and splits the input by whitespace into a fixed number of arguments.
     * <p>
     * If the user's input contains fewer parts than specified by {@code numOfArgs},
     * the remaining arguments will be returned as empty strings.
     * </p>
     *
     * @param message   the message to display to the user.
     * @param numOfArgs the expected number of arguments.
     * @return an array of strings representing the user's input.
     */
    public String[] getUserInput(String message, int numOfArgs) {
        System.out.println(message);
        // Read a full line of input from the user.
        String input = scanner.nextLine();
        // Split the input at whitespace(s).
        String[] parts = input.trim().split("\\s+");
        String[] result = new String[numOfArgs];
        
        // Populate result array with user input or empty strings if missing.
        for (int i = 0; i < numOfArgs; i++) {
            result[i] = (i < parts.length) ? parts[i] : "";
        }
        
        return result;
    }

    /**
     * Displays the main menu for non-authenticated users.
     */
    public void mainMenu() {
        System.out.println("====== E-Commerce System ======\n");
        System.out.println("1. Login\n");
        System.out.println("2. Register\n");
        System.out.println("3. Quit\n");
        System.out.println("===========================");
    }

    /**
     * Displays the admin menu with available admin operations.
     */
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

    /**
     * Displays the customer menu with available customer operations.
     */
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

    /**
     * Displays a list of objects with pagination information.
     * <p>
     * The displayed list header includes the user role, the list type, and the current page details.
     * Each object in the list is printed in order.
     * </p>
     *
     * @param userRole   the role of the user viewing the list (e.g., admin or customer).
     * @param listType   a descriptive name for the list (e.g., "Product", "Order").
     * @param objectList the list of objects to display.
     * @param pageNumber the current page number being displayed.
     * @param totalPages the total number of available pages.
     */
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

    /**
     * Prints an error message to the console, prefixed with the error source.
     *
     * @param errorSource  the source or context of the error.
     * @param errorMessage the detailed error message.
     */
    public void printErrorMessage(String errorSource, String errorMessage) {
        System.out.println("[Error] " + errorSource + ": " + errorMessage);
    }

    /**
     * Prints a general message to the console.
     *
     * @param message the message to be printed.
     */
    public void printMessage(String message) {
        System.out.println(message);
    }

    /**
     * Prints the string representation of a target object.
     *
     * @param targetObject the object whose string representation is to be printed.
     */
    public void printObject(Object targetObject) {
        System.out.println(targetObject.toString());
    }
}