import java.util.List;

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
                case 1 -> handleLogin();
                case 2 -> handleRegistration();
                case 3 -> {
                    io.printMessage("Exiting application. Goodbye!");
                    System.exit(0);
                }
                default -> io.printErrorMessage("Main Menu", "Invalid choice. Please try again!");
            }
        }
    }

    private static void handleLogin() {
        String[] loginData = io.getUserInput("Enter username and password", 2);
        String username = loginData[0];
        String password = loginData[1];

        if ("admin".equalsIgnoreCase(username) && "admin123".equals(password)) {
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
                case 1 -> io.showList("Admin", "Products", ProductOperation.getInstance().getProductList(1).getProducts(), 1, 1);
                case 2 -> handleRegistration();
                case 3 -> io.showList("Admin", "Customers", CustomerOperation.getInstance().getCustomerList(1).getCustomers(), 1, 1);
                case 4 -> io.showList("Admin", "Orders", OrderOperation.getInstance().getOrderList("all", 1).getOrders(), 1, 1);
                case 5 -> {
                    OrderOperation.getInstance().generateTestOrderData();
                    io.printMessage("Test data generated.");
                }
                case 6 -> generateAllFigures();
                case 7 -> deleteAllData();
                case 8 -> {
                    io.printMessage("Logging out...");
                    return;
                }
                default -> io.printErrorMessage("Admin Menu", "Invalid choice. Try again.");
            }
        }
    }

    private static void customerMenu(String customerId) {
        while (true) {
            io.customerMenu();
            String[] customerChoice = io.getUserInput("Enter menu option", 1);
            int choice = parseInteger(customerChoice[0]);

            switch (choice) {
                case 1 -> io.printObject(CustomerOperation.getInstance().getCustomerById(customerId));
                case 2 -> handleProfileUpdate(customerId);
                case 3 -> handleProductSearch();
                case 4 -> io.showList("Customer", "Orders", OrderOperation.getInstance().getOrderList(customerId, 1).getOrders(), 1, 1);
                case 5 -> {
                    OrderOperation.getInstance().generateSingleCustomerConsumptionFigure(customerId);
                    io.printMessage("Consumption figures generated.");
                }
                case 6 -> {
                    io.printMessage("Logging out...");
                    return;
                }
                default -> io.printErrorMessage("Customer Menu", "Invalid choice.");
            }
        }
    }

    private static void handleProfileUpdate(String customerId) {
        io.printMessage("Updating profile for customer: " + customerId);
        
        String[] profileData = io.getUserInput("Enter new username and password", 2);
        String newUsername = profileData[0];
        String newPassword = profileData[1];
        
        Customer customer = CustomerOperation.getInstance().getCustomerById(customerId);
        boolean success = CustomerOperation.getInstance().updateProfile(newUsername, newPassword, customer);
        if (success) {
            io.printMessage("Profile updated successfully!");
        } else {
            io.printErrorMessage("Profile Update", "Failed to update profile.");
        }
    }

    private static void handleProductSearch() {
        String[] searchKeyword = io.getUserInput("Enter product keyword", 1);
        List<Product> matchingProducts = ProductOperation.getInstance().getProductListByKeyword(searchKeyword[0]);

        if (!matchingProducts.isEmpty()) {
            io.showList("Customer", "Search Results", matchingProducts, 1, 1);
        } else {
            io.printErrorMessage("Product Search", "No matching products found.");
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

    private static void generateAllFigures() {
        io.printMessage("Generating all figures...");

        // Product-related figures
        ProductOperation.getInstance().generateCategoryFigure();
        ProductOperation.getInstance().generateDiscountFigure();
        ProductOperation.getInstance().generateLikesCountFigure();
        ProductOperation.getInstance().generateDiscountLikesCountFigure();

        // Order-related figures
        OrderOperation.getInstance().generateAllTop10BestSellersFigure();
        OrderOperation.getInstance().generateAllCustomersConsumptionFigure();

        io.printMessage("All figures successfully generated!");
    }

    private static void deleteAllData() {
        io.printMessage("Deleting all data...");

    
        ProductOperation.getInstance().deleteAllProducts();
        OrderOperation.getInstance().deleteAllOrders();
        CustomerOperation.getInstance().deleteAllCustomers();

        io.printMessage("All data successfully deleted!");
    }
}