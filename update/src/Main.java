public class Main {
    public static void main(String[] args) {
        IOinterface ioInterface = IOinterface.getInstance(); // Handles user interaction
        UserOperation userOperation = UserOperation.getInstance(); // Manages user-related operations
        ProductOperation productOperation = ProductOperation.getInstance(); // Manages product-related operations
        OrderOperation orderOperation = OrderOperation.getInstance(); // Handles order operations

        ioInterface.printMessage("====== E-Commerce System ======");

        AdminOperation adminOperation = AdminOperation.getInstance();
        adminOperation.registerAdmin();

        ioInterface.mainMenu();
    }
}