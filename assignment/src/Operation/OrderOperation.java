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

public class OrderOperation {

    private static OrderOperation instance;
    private JSONParser parser;

    private OrderOperation() {
        parser = new JSONParser();
    }

    public static OrderOperation getInstance() {
        if (instance == null) { instance = new OrderOperation(); }
        return instance;    
    }

    public String generateUniqueOrderId() {
        return "o_" + String.format("%05d", new Random().nextInt(100000));
    }

    public boolean createAnOrder(String customerId, String productId, String createTime) {
        if (createTime == null || createTime.isEmpty()) {
            createTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy_HH:mm:ss"));
        }
        String orderId = generateUniqueOrderId();

        
        HashMap<String,Object> orderDetails = new HashMap<String,Object>();
        orderDetails.put("order_id", orderId);
        orderDetails.put("user_id", customerId);
        orderDetails.put("pro_id", productId);
        orderDetails.put("order_time", createTime);
        JSONObject orderDetailsJSON = new JSONObject(orderDetails);

        try (FileWriter writer = new FileWriter("assignment/data/orders.txt", true)) {
            writer.write(orderDetailsJSON.toJSONString() + System.lineSeparator());
            return true;
        } catch (IOException ex) {
            System.err.println("Error creating order: " + ex.getMessage());
            return false;
        }
    }

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
                        continue; // Skip adding this record.
                    }
                } catch (ParseException pe) {
                    // If parsing fails, preserve the line.
                }
                lines.add(line);
            }
        } catch (IOException ex) {
            System.err.println("Error reading orders file: " + ex.getMessage());
            return false;
        }

        if (!found) {
            System.out.println("Order with id " + orderId + " not found.");
            return false;
        }

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

  
    public OrderListResult getOrderList(String customerId, int pageNumber) {
    List<String> lines = new ArrayList<>();
    File file = new File("assignment/data/orders.txt");
    
    if (!file.exists()) {
        System.err.println("Orders file not found: " + file.getAbsolutePath());
        return new OrderListResult(new ArrayList<>(), 1, 1);
    }
    
    // Read file and add valid lines.
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
    
    // Process orders from each valid JSON line.
    List<Order> allOrders = new ArrayList<>();
    for (String line : lines) {
        try {
            JSONObject json = (JSONObject) parser.parse(line);
            // If customerId equals "all", include all orders.
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
            System.err.println("Error parsing order record on line: \"" + line + "\". Exception: " + e);
        } catch (Exception e) {
            System.err.println("Error processing order record on line: \"" + line + "\". Exception: " + e);
        }
    }
    
    int totalOrders = allOrders.size();
    
    // If no orders exist, return a default result
    if (totalOrders == 0) {
        return new OrderListResult(new ArrayList<>(), 1, 1);
    }
    
    // Calculate total pages (10 orders per page).
    int totalPages = (int) Math.ceil(totalOrders / 10.0);
    // Clamp page number:
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


  
    public void generateTestOrderData() {
        String[] customerIds = {
        "u_0000000002", "u_0000000003", "u_0000000004",
        "u_0000000005", "u_0000000006", "u_0000000007",
        "u_0000000008", "u_0000000009", "u_0000000010",
        "u_0000000011"
        };
    
        var productResult = ProductOperation.getInstance().getProductList(1);
        List<Product> availableProducts = productResult.products;
        if (availableProducts == null || availableProducts.isEmpty()) {
            availableProducts = new ArrayList<>();
            availableProducts.add(new Product("p_dummy", "dummyModel", "dummyCategory", "Dummy Product", 0.0, 0.0, 0.0, 0));
        }
    
        deleteAllOrders();
    
        java.util.Random rand = new java.util.Random();
    
        for (String customerId : customerIds) {
        int numOrders = 50 + rand.nextInt(151);
        for (int i = 0; i < numOrders; i++) {
            Product p = availableProducts.get(rand.nextInt(availableProducts.size()));
            String productId = p.getProId();
            
            int year = 2024;
            int month = 1 + rand.nextInt(12);
            int day = 1 + rand.nextInt(28);
            int hour = rand.nextInt(24);
            int minute = rand.nextInt(60);
            int second = rand.nextInt(60);
            String orderTime = String.format("%02d-%02d-%04d_%02d:%02d:%02d", day, month, year, hour, minute, second);
            
            createAnOrder(customerId, productId, orderTime);
            }
        }
    }

    private List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        File file = new File("assignment/data/orders.txt");
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;
                try {
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

    // Deletes all orders by clearing the orders.txt file.
    public void deleteAllOrders() {
        File file = new File("assignment/data/orders.txt");
        try (FileWriter writer = new FileWriter(file, false)) {
            // Overwrite file with nothing to clear it.
        } catch (IOException ex) {
            System.err.println("Error clearing orders file: " + ex.getMessage());
        }
    }
    
    // Helper method: Get all orders for a given customer.
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
    
    public void generateSingleCustomerConsumptionFigure(String customerId) {
        new JFXPanel(); 
        Platform.runLater(() -> {
            List<Order> customerOrders = getAllOrdersForCustomer(customerId);
            // Prepare a map for consumption where keys = month (1–12), values = total consumption
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
            // Build a BarChart for consumption over 12 months
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
    
    public void generateAllCustomersConsumptionFigure() {
        new JFXPanel();
        Platform.runLater(() -> {
            List<Order> allOrders = getAllOrders();
            // Prepare consumption map for months 1-12.
            Map<Integer, Double> consumption = new HashMap<>();
            for (int m = 1; m <= 12; m++) {
                consumption.put(m, 0.0);
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH:mm:ss");
            for (Order order : allOrders) {
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
            yAxis.setLabel("Total Consumption ($)");
            BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
            barChart.setTitle("Consumption for All Customers");
            
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            for (int m = 1; m <= 12; m++) {
                series.getData().add(new XYChart.Data<>(String.valueOf(m), consumption.get(m)));
            }
            barChart.getData().add(series);
            
            Scene scene = new Scene(new StackPane(barChart), 800, 600);
            WritableImage image = scene.snapshot(null);
            File folder = new File("assignment/data/figure");
            if (!folder.exists()) folder.mkdirs();
            File outputFile = new File(folder, "all_customers_consumption.png");
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", outputFile);
                System.out.println("All customers consumption chart generated: " + outputFile.getAbsolutePath());
            } catch (IOException ex) {
                System.err.println("Error saving chart: " + ex.getMessage());
            }
        });
    }
    
    public void generateAllTop10BestSellersFigure() {
        new JFXPanel();
        Platform.runLater(() -> {
            List<Order> allOrders = getAllOrders();
            // Count orders per product.
            Map<String, Integer> productCounts = new HashMap<>();
            for (Order order : allOrders) {
                String prodId = order.getProId();
                productCounts.put(prodId, productCounts.getOrDefault(prodId, 0) + 1);
            }
            // Sort entries by count descending.
            List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(productCounts.entrySet());
            sortedEntries.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));
            int topN = Math.min(10, sortedEntries.size());
            List<Map.Entry<String, Integer>> top10 = sortedEntries.subList(0, topN);
            
            // Build a BarChart: x-axis = product name, y-axis = order count.
            CategoryAxis xAxis = new CategoryAxis();
            xAxis.setLabel("Product");
            NumberAxis yAxis = new NumberAxis();
            yAxis.setLabel("Order Count");
            BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
            barChart.setTitle("Top 10 Best-Selling Products");
            
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            for (Map.Entry<String, Integer> entry : top10) {
                // Look up the product name; fallback to product ID if not found.
                Product product = ProductOperation.getInstance().getProductById(entry.getKey());
                String productName = (product != null) ? product.getProName() : entry.getKey();
                series.getData().add(new XYChart.Data<>(productName, entry.getValue()));
            }
            barChart.getData().add(series);
            
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
    
    public static class OrderListResult {
        public List<Order> orders;
        public int currentPage;
        public int totalPages;
    
        public OrderListResult(List<Order> orders, int currentPage, int totalPages) {
            this.orders = orders;
            this.currentPage = currentPage;
            this.totalPages = totalPages;
        }
    }
}

