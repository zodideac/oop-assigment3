public class Main {
    private static IOinterface io = IOinterface.getInstance();
    public static void main(String[] args) {
        try {
            startApplication();
        } catch (Exception e) {
            io.printErrorMessage("Main Application", "Unexpected error: " + e.getMessage());
        }
    }

    private static void startApplication() {
        while (true) {
            io.mainMenu();
            String[] userChoice = io.getUserInput("Enter your choice", 1);
            int choice = parseInteger(userChoice[0]);

            switch (choice) {
                case 1:
                    handleLogin();
                    break;
                case 2:
                    handleRegistration();
                    break;
                case 3:
                    io.printMessage("Exiting application. Goodbye!");
                    System.exit(0);
                default:
                    io.printErrorMessage("Main Menu", "Invalid choice. Please try again.");
            }
        }
    }

    private static void handleLogin() {
        String[] loginData = io.getUserInput("Enter username and password", 2);
        String username = loginData[0];
        String password = loginData[1];

        if (username.equalsIgnoreCase("admin") && password.equals("adminpass")) {
            io.printMessage("Login successful. Welcome, admin!");
            adminMenu();
        } else {
            io.printErrorMessage("Login", "Invalid credentials. Try again.");
        }
    }

    private static void handleRegistration() {
        io.printMessage("Registering a new customer.");
        String[] customerData = io.getUserInput("Enter Username, Password, Email, Mobile", 4);

        io.printMessage("Customer registered successfully!");
    }

    private static void adminMenu() {
        while (true) {
            io.adminMenu();
            String[] adminChoice = io.getUserInput("Enter your choice", 1);
            int choice = parseInteger(adminChoice[0]);

            switch (choice) {
                case 1:
                    io.printMessage("Showing products...");
                    break;
                case 2:
                    handleRegistration();
                    break;
                case 3:
                    io.printMessage("Showing customers...");
                    break;
                case 4:
                    io.printMessage("Showing orders...");
                    break;
                case 5:
                    io.printMessage("Generating test data...");
                    break;
                case 6:
                    io.printMessage("Generating statistical figures...");
                    break;
                case 7:
                    io.printMessage("Deleting all data...");
                    break;
                case 8:
                    io.printMessage("Logging out...");
                    return;
                default:
                    io.printErrorMessage("Admin Menu", "Invalid choice. Try again.");
            }
        }
    }

    private static void customerMenu() {
        while (true) {
            io.customerMenu();
            String[] customerChoice = io.getUserInput("Enter your choice", 1);
            int choice = parseInteger(customerChoice[0]);

            switch (choice) {
                case 1:
                    io.printMessage("Showing profile...");
                    break;
                case 2:
                    io.printMessage("Updating profile...");
                    break;
                case 3:
                    io.printMessage("Showing products...");
                    break;
                case 4:
                    io.printMessage("Showing history orders...");
                    break;
                case 5:
                    io.printMessage("Generating consumption figures...");
                    break;
                case 6:
                    io.printMessage("Logging out...");
                    return;
                default:
                    io.printErrorMessage("Customer Menu", "Invalid choice.");
            }
        }
    }

    private static int parseInteger(String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            io.printErrorMessage("Input Parsing", "Invalid number format. Defaulting to -1.");
            return -1;
        }
    }
}