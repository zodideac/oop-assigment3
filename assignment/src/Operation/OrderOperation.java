package Operation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import org.json.simple.JSONValue;
import java.util.LinkedHashMap;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import Model.Order;
import Model.Product;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.BarChart;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javax.imageio.ImageIO;

/**
 * The {@code OrderOperation} class provides functionality for managing orders.
 * This includes creating, deleting, and retrieving paginated order lists.
 * <p>
 * The class follows the singleton pattern to ensure all order-related operations are handled by a single instance.
 * Order data is stored in "assignment/data/orders.txt" in JSON format.
 * </p>
 */
public class OrderOperation {

    /** Singleton instance of OrderOperation. */
    private static OrderOperation instance;

    /** JSON parser for processing order data in JSON format. */
    private JSONParser parser;

    /**
     * Private constructor to enforce singleton pattern.
     * Initializes the JSON parser.
     */
    private OrderOperation() {
        parser = new JSONParser();
    }

    /**
     * Retrieves the singleton instance of {@code OrderOperation}.
     * If an instance does not exist, one is created.
     *
     * @return the singleton instance of OrderOperation.
     */
    public static OrderOperation getInstance() {
        if (instance == null) {
            instance = new OrderOperation();
        }
        return instance;
    }

    /**
     * Generates a unique order ID.
     * <p>
     * The order ID is prefixed with "o_" followed by a randomly generated 5-digit number.
     * </p>
     *
     * @return a unique order ID.
     */
    public String generateUniqueOrderId() {
        return "o_" + String.format("%05d", new Random().nextInt(100000));
    }

    /**
 * Creates an order record and writes it to the orders file using a fixed JSON key order.
 *
 * @param customerId the customer ID.
 * @param productId  the product ID.
 * @param orderTime  the order timestamp.
 * @return true if the order was written successfully; false otherwise.
 */

    public boolean createAnOrder(String customerId, String productId, String orderTime) {
    // Generate a random unique order ID. (Adjust this as needed.)
    String orderId = "o_" + String.format("%05d", new java.util.Random().nextInt(100000));
    
    // Build the order record using a LinkedHashMap to preserve key order.
    LinkedHashMap<String, Object> orderMap = new LinkedHashMap<>();
    orderMap.put("order_id", orderId);
    orderMap.put("user_id", customerId);
    orderMap.put("pro_id", productId);
    orderMap.put("order_time", orderTime);
    
    // Define the desired order for the keys.
    List<String> keyOrder = List.of("order_id", "user_id", "pro_id", "order_time");
    
    // Generate the JSON string using our helper.
    String orderJSON = toOrderedJSONString(orderMap, keyOrder);
    
    // Write the JSON string to the orders.txt file.
    File file = new File("assignment/data/orders.txt");
    try (FileWriter writer = new FileWriter(file, true)) {
        writer.write(orderJSON + System.lineSeparator());
        return true;
    } catch (IOException e) {
        System.err.println("Error writing order to file: " + e.getMessage());
        return false;
    }
}


    /**
     * Deletes an order by its order ID.
     * <p>
     * The method reads all order records, removes the matching order, and rewrites the file.
     * </p>
     *
     * @param orderId the unique identifier of the order to delete.
     * @return {@code true} if the order was found and deleted; {@code false} otherwise.
     */
    public boolean deleteOrder(String orderId) {
        List<String> lines = new ArrayList<>();
        File file = new File("assignment/data/orders.txt");
        boolean found = false;

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                try {
                    JSONObject json = (JSONObject) parser.parse(line);
                    if (orderId.equals(json.get("order_id"))) {
                        found = true;
                        continue; // Skip this record.
                    }
                } catch (ParseException pe) {
                    // Preserve unparseable lines.
                }
                lines.add(line);
            }
        } catch (IOException ex) {
            System.err.println("Error reading orders file: " + ex.getMessage());
            return false;
        }

        if (!found) {
            System.out.println("Order with ID " + orderId + " not found.");
            return false;
        }

        // Rewrite the orders file without the deleted order.
        try (FileWriter writer = new FileWriter("assignment/data/orders.txt", false)) {
            for (String ln : lines) {
                writer.write(ln + System.lineSeparator());
            }
            return true;
        } catch (IOException ex) {
            System.err.println("Error writing orders file: " + ex.getMessage());
            return false;
        }
    }

    /**
    * Helper class to encapsulate paginated order list results.
    */
    public static class OrderListResult {
        public final List<Order> orders;
        public final int currentPage;
        public final int totalPages;

        public OrderListResult(List<Order> orders, int currentPage, int totalPages) {
            this.orders = orders;
            this.currentPage = currentPage;
            this.totalPages = totalPages;
        }
    }

    /**
     * Retrieves a paginated list of orders for a specific customer or all customers.
     * <p>
     * If {@code customerId} is "all", orders for all customers are returned.
     * Orders are displayed in pages of 10 records each.
     * </p>
     *
     * @param customerId the unique identifier of the customer whose orders are requested. Use "all" for all orders.
     * @param pageNumber the page number to retrieve.
     * @return an {@code OrderListResult} containing the paginated order list.
     */
    public OrderListResult getOrderList(String customerId, int pageNumber) {
        List<String> lines = new ArrayList<>();
        File file = new File("assignment/data/orders.txt");

        if (!file.exists()) {
            System.err.println("Orders file not found: " + file.getAbsolutePath());
            return new OrderListResult(new ArrayList<>(), 1, 1);
        }

        // Read order records from the file.
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (!line.isEmpty()) {
                    lines.add(line);
                }
            }
        } catch (IOException ex) {
            System.err.println("Error reading orders file: " + ex.getMessage());
            return new OrderListResult(new ArrayList<>(), 1, 1);
        }

        List<Order> allOrders = new ArrayList<>();
        for (String line : lines) {
            try {
                JSONObject json = (JSONObject) parser.parse(line);
                if ("all".equalsIgnoreCase(customerId) || customerId.equals(json.get("user_id"))) {
                    String orderId = (String) json.get("order_id");
                    String userId = (String) json.get("user_id");
                    String proId = (String) json.get("pro_id");
                    String orderTime = (String) json.get("order_time");

                    if (orderId == null || userId == null || proId == null || orderTime == null) {
                        System.err.println("Missing key in order record: " + line);
                        continue;
                    }

                    allOrders.add(new Order(orderId, userId, proId, orderTime));
                }
            } catch (ParseException e) {
                System.err.println("Error parsing order record: \"" + line + "\". Exception: " + e);
            }
        }

        int totalOrders = allOrders.size();
        if (totalOrders == 0) {
            return new OrderListResult(new ArrayList<>(), 1, 1);
        }

        int totalPages = (int) Math.ceil(totalOrders / 10.0);
        if (pageNumber < 1) {
            pageNumber = 1;
        } else if (pageNumber > totalPages) {
            pageNumber = totalPages;
        }

        int start = (pageNumber - 1) * 10;
        int end = Math.min(start + 10, totalOrders);
        List<Order> pageOrders = allOrders.subList(start, end);

        return new OrderListResult(pageOrders, pageNumber, totalPages);
    }

    /**
 * Converts the given LinkedHashMap into a JSON string with keys output in the specified order.
 *
 * @param orderedMap the map containing keys and values.
 * @param keyOrder   the list of keys in the desired order.
 * @return a JSON formatted string with the keys in the specified order.
 */
private String toOrderedJSONString(LinkedHashMap<String, Object> orderedMap, List<String> keyOrder) {
    StringBuilder sb = new StringBuilder();
    sb.append("{");
    boolean first = true;
    // Iterate through the keys in the desired order.
    for (String key : keyOrder) {
        if (orderedMap.containsKey(key)) {
            if (!first) {
                sb.append(",");
            }
            sb.append("\"").append(key).append("\":");
            sb.append(JSONValue.toJSONString(orderedMap.get(key)));
            first = false;
        }
    }
    sb.append("}");
    return sb.toString();
}


    /**
 * Generates test order data for multiple customers.
 * <p>
 * The method assigns random products to customers and generates a random order date 
 * for each order. It ensures that existing order records are cleared before adding new test orders.
 * </p>
 */
public void generateTestOrderData() {
    // Sample customer IDs to generate test orders for.
    String[] customerIds = {
        "u_0000000002", "u_0000000003", "u_0000000004",
        "u_0000000005", "u_0000000006", "u_0000000007",
        "u_0000000008", "u_0000000009", "u_0000000010",
        "u_0000000011"
    };

    // Retrieve available products from ProductOperation.
    var productResult = ProductOperation.getInstance().getProductList(1);
    List<Product> availableProducts = productResult.products;

    // If there are no available products, create a dummy product for testing.
    if (availableProducts == null || availableProducts.isEmpty()) {
        availableProducts = new ArrayList<>();
        availableProducts.add(new Product("p_dummy", "dummyModel", "dummyCategory", "Dummy Product", 0.0, 0.0, 0.0, 0));
    }

    // Delete all previous order records before generating new test data.
    deleteAllOrders();

    java.util.Random rand = new java.util.Random();

    // Generate random orders for each customer.
    for (String customerId : customerIds) {
        int numOrders = 50 + rand.nextInt(151); // Each customer gets between 50–200 orders.
        for (int i = 0; i < numOrders; i++) {
            // Select a random product from the available list.
            Product p = availableProducts.get(rand.nextInt(availableProducts.size()));
            String productId = p.getProId();

            // Generate a random order timestamp.
            int year = 2024;
            int month = 1 + rand.nextInt(12);
            int day = 1 + rand.nextInt(28);
            int hour = rand.nextInt(24);
            int minute = rand.nextInt(60);
            int second = rand.nextInt(60);
            String orderTime = String.format("%02d-%02d-%04d_%02d:%02d:%02d", day, month, year, hour, minute, second);

            // Create the order.
            createAnOrder(customerId, productId, orderTime);
        }
    }
}

/**
 * Retrieves all orders from the orders file.
 * <p>
 * The method reads "assignment/data/orders.txt" and parses JSON records into a list of {@code Order} objects.
 * </p>
 *
 * @return a list containing all orders.
 */
private List<Order> getAllOrders() {
    List<Order> orders = new ArrayList<>();
    File file = new File("assignment/data/orders.txt");

    // Read order records from the file.
    try (Scanner scanner = new Scanner(file)) {
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) continue;
            try {
                // Parse JSON order data.
                JSONObject json = (JSONObject) parser.parse(line);
                String orderId = (String) json.get("order_id");
                String userId = (String) json.get("user_id");
                String proId = (String) json.get("pro_id");
                String orderTime = (String) json.get("order_time");
                orders.add(new Order(orderId, userId, proId, orderTime));
            } catch (ParseException pe) {
                System.err.println("Error parsing order record: " + pe.getMessage());
            }
        }
    } catch (IOException ex) {
        System.err.println("Error reading orders file: " + ex.getMessage());
    }
    return orders;
}

/**
 * Deletes all orders by clearing the orders file.
 */
public void deleteAllOrders() {
    File file = new File("assignment/data/orders.txt");
    try (FileWriter writer = new FileWriter(file, false)) {
        // Overwrite file with an empty string to delete all records.
    } catch (IOException ex) {
        System.err.println("Error clearing orders file: " + ex.getMessage());
    }
}

/**
 * Retrieves all orders associated with a specific customer.
 * <p>
 * The method filters orders based on customer ID.
 * </p>
 *
 * @param customerId the unique identifier of the customer.
 * @return a list of orders associated with the given customer.
 */
private List<Order> getAllOrdersForCustomer(String customerId) {
    List<Order> allOrders = getAllOrders();
    List<Order> customerOrders = new ArrayList<>();
    for (Order o : allOrders) {
        if (customerId.equals(o.getUserId())) {
            customerOrders.add(o);
        }
    }
    return customerOrders;
}

/**
 * Generates a bar chart depicting monthly consumption for a single customer.
 * <p>
 * The method aggregates order prices per month and creates a BarChart using JavaFX.
 * The chart is saved as an image in "assignment/data/figure".
 * </p>
 *
 * @param customerId the unique identifier of the customer.
 */
    public void generateSingleCustomerConsumptionFigure(String customerId) {
    new JFXPanel(); 
    Platform.runLater(() -> {
        List<Order> customerOrders = getAllOrdersForCustomer(customerId);
        
        Map<Integer, Double> consumption = new HashMap<>();
        for (int m = 1; m <= 12; m++) {
            consumption.put(m, 0.0);
        }
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH:mm:ss");
        for (Order order : customerOrders) {
            try {
                LocalDateTime orderTime = LocalDateTime.parse(order.getOrderTime(), formatter);
                int month = orderTime.getMonthValue();
                Product product = ProductOperation.getInstance().getProductById(order.getProId());
                if (product != null) {
                    double price = product.getProCurrentPrice();
                    consumption.put(month, consumption.get(month) + price);
                }
            } catch (Exception e) {
                System.err.println("Error processing order " + order.getOrderId() + ": " + e.getMessage());
            }
        }

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Month");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Consumption ($)");
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Consumption for Customer " + customerId);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (int m = 1; m <= 12; m++) {
            series.getData().add(new XYChart.Data<>(String.valueOf(m), consumption.get(m)));
        }
        barChart.getData().add(series);

        Scene scene = new Scene(new StackPane(barChart), 800, 600);
        WritableImage image = scene.snapshot(null);
        File folder = new File("assignment/data/figure");
        if (!folder.exists()) folder.mkdirs();
        File outputFile = new File(folder, "single_customer_consumption_" + customerId + ".png");
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", outputFile);
            System.out.println("Single customer consumption chart generated: " + outputFile.getAbsolutePath());
        } catch (IOException ex) {
            System.err.println("Error saving chart: " + ex.getMessage());
        }
    });
    }

    /**
 * Generates a bar chart that displays the total consumption for all customers over a 12-month period.
 * <p>
 * This method retrieves all orders, aggregates the order amounts by month (using each product's current price),
 * and then constructs a BarChart using JavaFX to visualize monthly consumption. The resulting chart is saved as a PNG
 * image to "assignment/data/figure/all_customers_consumption.png".
 * </p>
 */
public void generateAllCustomersConsumptionFigure() {
    // Initialize the JavaFX toolkit.
    new JFXPanel();
    
    // Ensure JavaFX operations run on the JavaFX Application Thread.
    Platform.runLater(() -> {
        // Retrieve all orders from the data store.
        List<Order> allOrders = getAllOrders();
        
        // Prepare a consumption map that will hold total consumption for each month (1 to 12).
        Map<Integer, Double> consumption = new HashMap<>();
        for (int m = 1; m <= 12; m++) {
            consumption.put(m, 0.0);
        }
        
        // Define a formatter to parse the order timestamp.
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH:mm:ss");
        
        // Iterate over each order to accumulate consumption data.
        for (Order order : allOrders) {
            try {
                // Parse the order time into a LocalDateTime object.
                LocalDateTime orderTime = LocalDateTime.parse(order.getOrderTime(), formatter);
                int month = orderTime.getMonthValue();
                
                // Retrieve the product associated with this order.
                Product product = ProductOperation.getInstance().getProductById(order.getProId());
                if (product != null) {
                    double price = product.getProCurrentPrice();
                    // Update the consumption total for the corresponding month.
                    consumption.put(month, consumption.get(month) + price);
                }
            } catch (Exception e) {
                System.err.println("Error processing order " + order.getOrderId() + ": " + e.getMessage());
            }
        }
        
        // Set up the chart axes.
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Month");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Total Consumption ($)");
        
        // Create a BarChart with the specified axes.
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Consumption for All Customers");
        
        // Build a data series from the consumption map.
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (int m = 1; m <= 12; m++) {
            series.getData().add(new XYChart.Data<>(String.valueOf(m), consumption.get(m)));
        }
        barChart.getData().add(series);
        
        // Create a Scene containing the bar chart.
        Scene scene = new Scene(new StackPane(barChart), 800, 600);
        // Take a snapshot of the Scene.
        WritableImage image = scene.snapshot(null);
        
        // Ensure the output folder exists.
        File folder = new File("assignment/data/figure");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        // Define the output file.
        File outputFile = new File(folder, "all_customers_consumption.png");
        
        // Save the snapshot as a PNG image.
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", outputFile);
            System.out.println("All customers consumption chart generated: " + outputFile.getAbsolutePath());
        } catch (IOException ex) {
            System.err.println("Error saving chart: " + ex.getMessage());
        }
    });
}
    
    /**
 * Generates a bar chart showing the top 10 best-selling products based on order counts.
 * <p>
 * The method retrieves all orders, counts occurrences of each product,
 * sorts them by order count, and visualizes the top 10 best sellers using JavaFX.
 * The resulting chart is saved as an image file in "assignment/data/figure/top10_bestsellers.png".
 * </p>
 */
public void generateAllTop10BestSellersFigure() {
    // Initialize JavaFX environment.
    new JFXPanel();
    
    // Run JavaFX operations on the JavaFX Application Thread.
    Platform.runLater(() -> {
        List<Order> allOrders = getAllOrders();

        // Count occurrences of each product in orders.
        Map<String, Integer> productCounts = new HashMap<>();
        for (Order order : allOrders) {
            String prodId = order.getProId();
            productCounts.put(prodId, productCounts.getOrDefault(prodId, 0) + 1);
        }

        // Sort products by order count in descending order.
        List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(productCounts.entrySet());
        sortedEntries.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

        // Extract the top 10 best-selling products.
        int topN = Math.min(10, sortedEntries.size());
        List<Map.Entry<String, Integer>> top10 = sortedEntries.subList(0, topN);

        // Set up BarChart axes.
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Product");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Order Count");

        // Create the BarChart.
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Top 10 Best-Selling Products");

        // Populate the chart with data.
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (Map.Entry<String, Integer> entry : top10) {
            // Retrieve product name; fallback to product ID if name not found.
            Product product = ProductOperation.getInstance().getProductById(entry.getKey());
            String productName = (product != null) ? product.getProName() : entry.getKey();
            series.getData().add(new XYChart.Data<>(productName, entry.getValue()));
        }
        barChart.getData().add(series);

        // Create a JavaFX scene, capture the chart snapshot, and save the image.
        Scene scene = new Scene(new StackPane(barChart), 800, 600);
        WritableImage image = scene.snapshot(null);
        File folder = new File("assignment/data/figure");
        if (!folder.exists()) folder.mkdirs();
        File outputFile = new File(folder, "top10_bestsellers.png");

        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", outputFile);
            System.out.println("Top 10 best sellers chart generated: " + outputFile.getAbsolutePath());
        } catch (IOException ex) {
            System.err.println("Error saving chart: " + ex.getMessage());
        }
    });
    }
}

