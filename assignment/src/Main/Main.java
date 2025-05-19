package Main;

import java.io.File;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

import IO.IOinterface;
import Model.Customer;
import Model.User;
import Operation.AdminOperation;
import Operation.CustomerOperation;
import Operation.OrderOperation;
import Operation.OrderOperation.OrderListResult;
import Operation.ProductOperation;
import Operation.UserOperation;
import Operation.ProductOperation.ProductListResult;
import javafx.application.Platform;

/**
 * The {@code Main} class serves as the entry point for the e-commerce system.
 * <p>
 * It sets up the necessary environment by filtering out specific error messages,
 * ensures that the default admin account is registered, initializes JavaFX, and then
 * presents a main menu for the user. Based on user input, it handles login and 
 * registration, and dispatches the user to either an admin or customer menu.
 * </p>
 */
public class Main {
  
    /** Singleton instance for I/O operations. */
    private static final IOinterface io = IOinterface.getInstance();

    /** Scanner for reading console input. */
    private static final Scanner scanner = new Scanner(System.in);

    /**
     * Main method: the entry point of the application.
     * <p>
     * It configures an error output filter to remove unwanted JavaFX configuration warnings,
     * registers the default admin account if not present, starts the JavaFX platform (required for chart generation),
     * and then enters a loop displaying the main menu, allowing users to log in or register.
     * Depending on the login result, it routes the user to the admin or customer menu.
     * </p>
     *
     * @param args command-line arguments (not used).
     */
    public static void main(String[] args) {
        // Store the original error stream.
        PrintStream originalErr = System.err;
        
        // Filter the error stream to ignore specific warnings (e.g., Unsupported JavaFX configuration).
        System.setErr(new PrintStream(new FilterOutputStream(originalErr) {
            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                String message = new String(b, off, len);
                // Only print the message if it does not contain the unwanted warning text.
                if (!message.contains("Unsupported JavaFX configuration")) {
                    out.write(b, off, len);
                }
            }
            @Override
            public void write(int b) throws IOException {
                out.write(b);
            }
        }));

        // Ensure the admin account is registered.
        AdminOperation.getInstance().registerAdmin();

        // Print the working directory of the users file for debugging.
        System.out.println("Working Directory = " + new File("assignment/data/users.txt").getAbsolutePath());

        // Initialize the JavaFX platform for chart generation.
        Platform.startup(() -> {});

        boolean exit = false;
        // Main loop for displaying the main menu.
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
                    
                    // Attempt to login.
                    User user = UserOperation.getInstance().login(username, password);
                    if (user == null) {
                        io.printErrorMessage("Login", "Invalid credentials. Please try again.");
                    } else {
                        io.printMessage("Login successful. Welcome, " + user.getUserName() + "!");
                        // Dispatch to the correct menu based on user role.
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
                    
                    // Register the customer using CustomerOperation.
                    boolean regSuccess = CustomerOperation.getInstance()
                                                .registerCustomer(regUsername, regPassword, email, mobile);
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

    
    /**
     * Displays the admin menu and processes admin-specific actions.
     * <p>
     * The menu allows the admin to:
     * <ul>
     *   <li>Show products by page number.</li>
     *   <li>Add new customers.</li>
     *   <li>Display a paginated list of customers.</li>
     *   <li>Display a paginated list of all orders.</li>
     *   <li>Generate test order data.</li>
     *   <li>Generate various statistical figures.</li>
     *   <li>Delete all system data.</li>
     *   <li>Logout and return to the main menu.</li>
     * </ul>
     * </p>
     *
     * @param adminUser the admin user currently logged in.
     */
    private static void adminMenu(User adminUser) {
        boolean logout = false;
        while (!logout) {
            io.adminMenu();  // Display the admin menu on the console.
            System.out.print("Enter your choice: ");
            String option = scanner.nextLine().trim();
            switch (option) {
                case "1":
                    // Case 1: Show products (by page number)
                    paginateProductList(adminUser.getUserRole());
                    break;
                case "2":
                    // Case 2: Add a customer.
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
                case "3":
                    // Case 3: Show customers (paginated).
                    paginateCustomerList(adminUser.getUserRole());
                    break;
                case "4": 
                    // Case 4: Show orders for admin
                     paginateOrderList(adminUser.getUserRole(), "all");
                    break;
                case "5":
                    // Case 5: Generate test order data.
                    OrderOperation.getInstance().generateTestOrderData();
                    io.printMessage("Test order data generated successfully.");
                    break;
                case "6":
                    // Case 6: Generate all statistical figures.
                    generateAllFigures();
                    break;
                case "7":
                    // Case 7: Delete all system data.
                    deleteAllData();
                    break;
                case "8":
                    // Case 8: Logout.
                    io.printMessage("Logging out, returning to main menu.");
                    logout = true;
                    break;
                default:
                    io.printErrorMessage("Admin Menu", "Invalid option.");
                    break;
            }
        }
    }

    /**
     * Generates various statistical figures for the system.
     * <p>
     * This method triggers the generation of multiple figures including:
     * <ul>
     *   <li>Category distribution chart</li>
     *   <li>Discount distribution chart</li>
     *   <li>Likes count chart</li>
     *   <li>Discount vs. likes scatter chart</li>
     *   <li>Consumption figures for all customers</li>
     *   <li>Top 10 best-sellers chart</li>
     * </ul>
     * The figures are generated on the JavaFX Application Thread and saved to the data/figure folder.
     * </p */
    private static void generateAllFigures() {
        io.printMessage("Generating all figures...");
        // Use Platform.runLater() to ensure JavaFX operations execute on the correct thread.
        Platform.runLater(() -> {
            try {
                // Generate various figures by calling corresponding methods.
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

    /**
     * Deletes all data from the system including orders, customers, and products.
     * <p>
     * This method calls the delete operations for each entity to clear the corresponding files.
     * </p */
    private static void deleteAllData() {
        io.printMessage("Deleting all data...");
        // Delete all customers, products, and orders.
        CustomerOperation.getInstance().deleteAllCustomers();
        ProductOperation.getInstance().deleteAllProducts();
        OrderOperation.getInstance().deleteAllOrders();
        io.printMessage("All data successfully deleted!");
    }

    /**
     * Displays the customer menu and processes customer-specific actions.
     * <p>
     * The customer menu allows the customer to perform tasks such as:
     * <ul>
     *   <li>Viewing their profile</li>
     *   <li>Updating their profile</li>
     *   <li>Browsing products</li>
     *   <li>Viewing order history</li>
     *   <li>Generating consumption figures</li>
     *   <li>Logging out</li>
     * </ul>
     * Note: Implementation for full customer functionalities can be expanded further.
     * </p>
     *
     * @param customerUser the customer user who is logged in.
     */
    private static void customerMenu(User customerUser) {
        boolean logout = false;
        while (!logout) {
            io.customerMenu();
            System.out.print("Enter your choice: ");
            String option = scanner.nextLine().trim();
            switch (option) {
                case "1":
                    // Case 1: Display customer profile.
                    io.printObject(customerUser);
                    break;
                case "2":
                    // Case 2: Update profile attributes.
                    System.out.print("Enter the attribute to update (user_name, user_password, user_email, user_mobile): ");
                    String attr = scanner.nextLine().trim();
                    System.out.print("Enter new value: ");
                    String newVal = scanner.nextLine().trim();
                    boolean update = CustomerOperation.getInstance().updateProfile(attr, newVal, (Model.Customer) customerUser);
                    if (update) {
                        io.printMessage("Profile updated successfully.");
                    } else {
                        io.printErrorMessage("Update Profile", "Failed to update profile. Please check your input!");
                    }
                    break;
                case "3":
                    // Case 3: Show products (paginated).
                    paginateProductList(customerUser.getUserRole());
                    break;
                case "4":
                    // Case 4: Display order history.
                    paginateOrderList(customerUser.getUserRole(), customerUser.getUserId());
                    break;
                case "5":
                    // Case 5: Generate consumption figure.
                    OrderOperation.getInstance().generateSingleCustomerConsumptionFigure(customerUser.getUserId());
                    OrderOperation.getInstance().generateAllCustomersConsumptionFigure();
                    io.printMessage("Consumption figure generated. Please check the data/figure folder.");
                    break;
                case "6":
                    // Case 6: Logout.
                    io.printMessage("Logging out, returning to main menu.");
                    logout = true;
                    break;
                default:
                    io.printErrorMessage("Customer Menu", "Invalid option.");
                    break;
            }
        }
    }

    /**
    * Provides an interactive pagination session for product lists.
    *
    * @param userRole  The role of the user (used to choose the display format)
    */
    private static void paginateProductList(String userRole) {
        int currentPage = 1;
        while (true) {
            // Retrieve products for the current page
            ProductListResult pr = ProductOperation.getInstance().getProductList(currentPage);
            io.showList(userRole, "Product", pr.products, pr.currentPage, pr.totalPages);
            System.out.println("\nEnter 'n' for next page, 'p' for previous page, or 'b' to go back:");
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("n")) {
                if (currentPage < pr.totalPages) {
                    currentPage++;
                } else {
                    System.out.println("Already at the last page.");
                }
            } else if (input.equals("p")) {
                if (currentPage > 1) {
                    currentPage--;
                } else {
                    System.out.println("Already at the first page.");
                }
            } else if (input.equals("b")) {
                break;
            } else {
                System.out.println("Invalid input. Please enter 'n', 'p', or 'b'.");
            }
        }
    }

    /**
     * Provides an interactive pagination session for customer lists.
     * After displaying a page, the admin can use:
     *   - 'n' for next page,
     *   - 'p' for previous page,
     *   - 'b' to go back to the menu,
     *   - or enter a Customer ID to view that customer's details.
     *
     * @param userRole The role (e.g., "admin") for display purposes.
     */
    private static void paginateCustomerList(String userRole) {
        int currentPage = 1;
        
        while (true) {
            // Retrieve and display the customer list for the current page.
            var cr = CustomerOperation.getInstance().getCustomerList(currentPage);
            io.showList(userRole, "Customer", cr.customers, cr.currentPage, cr.totalPages);
            
            // Extend the prompt to allow viewing details by entering a customer ID.
            System.out.println("\nEnter 'n' for next page, 'p' for previous page, 'b' to go back,");
            System.out.println("or enter a Customer ID to view details:");
            String input = scanner.nextLine().trim();
            
            // Process navigation commands (use lower-case equals for command letters)
            if (input.equalsIgnoreCase("n")) {
                if (currentPage < cr.totalPages) {
                    currentPage++;
                } else {
                    System.out.println("Already at the last page.");
                }
            } else if (input.equalsIgnoreCase("p")) {
                if (currentPage > 1) {
                    currentPage--;
                } else {
                    System.out.println("Already at the first page.");
                }
            } else if (input.equalsIgnoreCase("b")) {
                break; // Exit the pagination session
            } else {
                // Assume the user entered a customer ID.
                // You need to implement/find a method to get a Customer by ID.
                Customer foundCustomer = CustomerOperation.getInstance().findCustomerById(input);
                if (foundCustomer != null) {
                    io.printObject(foundCustomer);
                    // Optionally, prompt to press enter to return to the list:
                    System.out.println("\nPress Enter to return to the customer list...");
                    scanner.nextLine();
                } else {
                    System.out.println("Customer with ID \"" + input + "\" not found on this page.");
                }
            }
        }
    }

    /**
     * Provides an interactive pagination session for order lists.
     *
     * @param userRole  The role of the user.
     * @param userId    The identifier for which to fetch orders. Use "all" for admin.
     */
    private static void paginateOrderList(String userRole, String userId) {
        int currentPage = 1;
        while (true) {
            OrderListResult olr = OrderOperation.getInstance().getOrderList(userId, currentPage);
            // The header below displays "Order" for admin (userId "all") or "Your Order" for a specific customer.
            String header = userId.equals("all") ? "Order" : "Your Order";
            io.showList(userRole, header, olr.orders, olr.currentPage, olr.totalPages);
            System.out.println("\nEnter 'n' for next page, 'p' for previous page, or 'b' to go back:");
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("n")) {
                if (currentPage < olr.totalPages) {
                    currentPage++;
                } else {
                    System.out.println("Already at the last page.");
                }
            } else if (input.equals("p")) {
                if (currentPage > 1) {
                    currentPage--;
                } else {
                    System.out.println("Already at the first page.");
                }
            } else if (input.equals("b")) {
                break;
            } else {
                System.out.println("Invalid input. Please enter 'n', 'p', or 'b'.");
            }
        }
    }
}



        
    
