package Main;

import java.util.List;
import java.io.File;
import java.util.Scanner;

import IO.IOinterface;
import Model.Order;
import Model.User;
import Operation.AdminOperation;
import Operation.CustomerOperation;
import Operation.OrderOperation;
import Operation.ProductOperation;
import Operation.UserOperation;
import Operation.ProductOperation.ProductListResult;
import javafx.application.Platform;

public class Main {
    private static final IOinterface io = IOinterface.getInstance();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        AdminOperation.getInstance().registerAdmin();

        System.out.println("Working Directory = " + new File("assignment/data/users.txt").getAbsolutePath());

        // Initialize JavaFX properly so that chart creation works.
        Platform.startup(() -> {
        });

        boolean exit = false;
        while (!exit) {
            io.mainMenu();
            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1": // Login
                    System.out.print("Username: ");
                    String username = scanner.nextLine().trim();
                    System.out.print("Password: ");
                    String password = scanner.nextLine().trim();
                    
                    User user = UserOperation.getInstance().login(username, password);
                    if (user == null) {
                        io.printErrorMessage("Login", "Invalid credentials. Please try again.");
                    } else {
                        io.printMessage("Login successful. Welcome, " + user.getUserName() + "!");
                        if (user.getUserRole().equalsIgnoreCase("admin")) {
                            adminMenu(user);
                        } else {
                            customerMenu(user);
                        }
                    }
                    break;
                case "2": // Register new customer
                    System.out.print("Enter username: ");
                    String regUsername = scanner.nextLine().trim();
                    System.out.print("Enter password: ");
                    String regPassword = scanner.nextLine().trim();
                    System.out.print("Enter email: ");
                    String email = scanner.nextLine().trim();
                    System.out.print("Enter mobile number: ");
                    String mobile = scanner.nextLine().trim();
                    
                    boolean regSuccess = CustomerOperation.getInstance().registerCustomer(regUsername, regPassword, email, mobile);
                    if (regSuccess) {
                        io.printMessage("Registration successful. You can now log in.");
                    } else {
                        io.printErrorMessage("Registration", "Registration failed. Username may already exist or data format is incorrect.");
                    }
                    break;
                case "3": // Quit
                    io.printMessage("Goodbye!");
                    exit = true;
                    break;
                default:
                    io.printErrorMessage("Main Menu", "Invalid choice. Please select 1, 2, or 3.");
            }
        }
        scanner.close();
    }
    
    private static void adminMenu(User adminUser) {
        boolean logout = false;
        while (!logout) {
            io.adminMenu();
            System.out.print("Enter your choice: ");
            String option = scanner.nextLine().trim();
            switch (option) {
                case "1": // Show products
                    System.out.print("Enter page number for product list: ");
                    int pPage = Integer.parseInt(scanner.nextLine().trim());
                    ProductListResult pr = ProductOperation.getInstance().getProductList(pPage);
                    io.showList(adminUser.getUserRole(), "Product", pr.products, pr.currentPage, pr.totalPages);
                    break;
                case "2": // Add customers
                    System.out.print("Enter new customer's username: ");
                    String username = scanner.nextLine().trim();
                    System.out.print("Enter password: ");
                    String pwd = scanner.nextLine().trim();
                    System.out.print("Enter email: ");
                    String email = scanner.nextLine().trim();
                    System.out.print("Enter mobile: ");
                    String mobile = scanner.nextLine().trim();
                    if (CustomerOperation.getInstance().registerCustomer(username, pwd, email, mobile)) {
                        io.printMessage("Customer added successfully.");
                    } else {
                        io.printErrorMessage("Add Customer", "Failed to add customer.");
                    }
                    break;
                case "3": // Show customers
                    System.out.print("Enter page number for customer list: ");
                    int cPage = Integer.parseInt(scanner.nextLine().trim());
                    var cr = CustomerOperation.getInstance().getCustomerList(cPage);
                    io.showList(adminUser.getUserRole(), "Customer", cr.customers, cr.currentPage, cr.totalPages);
                    break;
                case "4": // Show orders for admin
                    System.out.print("Enter page number for order list: ");
                    int oPage = Integer.parseInt(scanner.nextLine().trim());
                    List<Order> orders = Operation.OrderOperation.getInstance().getOrderList("all", oPage).orders;
                    io.showList(adminUser.getUserRole(), "Order", orders, oPage, 0);
                    break;
                case "5": // Generate test data
                    OrderOperation.getInstance().generateTestOrderData();
                    io.printMessage("Test order data generated successfully.");
                    break;
                case "6": // Generate statistical figures
                    generateAllFigures();
                    break;
                case "7": // Delete all data
                    deleteAllData();
                    break;
                case "8": // Logout
                    io.printMessage("Logging out, returning to main menu.");
                    logout = true;
                    break;
                default:
                    io.printErrorMessage("Admin Menu", "Invalid option.");
                    break;
            }
        }
    }
    
    private static void generateAllFigures() {
        io.printMessage("Generating all figures...");
        Platform.runLater(() -> {
            try {
                ProductOperation.getInstance().generateCategoryFigure();
                ProductOperation.getInstance().generateDiscountFigure();
                ProductOperation.getInstance().generateLikesCountFigure();
                ProductOperation.getInstance().generateDiscountLikesCountFigure();
                OrderOperation.getInstance().generateAllCustomersConsumptionFigure();
                OrderOperation.getInstance().generateAllTop10BestSellersFigure();
                io.printMessage("All statistical figures generated. Please check the data/figure folder.");
            } catch (Exception e) {
                io.printErrorMessage("Figure Generation", "Failed to generate figures: " + e.getMessage());
            }
        });
    }
    
    private static void deleteAllData() {
        io.printMessage("Deleting all data...");
        CustomerOperation.getInstance().deleteAllCustomers();
        ProductOperation.getInstance().deleteAllProducts();
        OrderOperation.getInstance().deleteAllOrders();
        io.printMessage("All data successfully deleted!");
    }
    
    private static void customerMenu(User customerUser) {
        boolean logout = false;
        while (!logout) {
            io.customerMenu();
            System.out.print("Enter your choice: ");
            String option = scanner.nextLine().trim();
            switch (option) {
                case "1":
                    io.printMessage("Customer menu option 1 selected.");
                    break;
                case "2":
                    io.printMessage("Customer menu option 2 selected.");
                    break;
                case "3":
                    io.printMessage("Logging out, returning to main menu.");
                    logout = true;
                    break;
                default:
                    io.printErrorMessage("Customer Menu", "Invalid option.");
                    break;
            }
        }
    }
}