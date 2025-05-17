import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import org.json.*;
import javafx.scene.chart.*;
import javafx.scene.image.WritableImage;
import javafx.embed.swing.SwingFXUtils;
import javax.imageio.ImageIO;
import javafx.scene.SnapshotParameters;


public class OrderOperation {
    private static OrderOperation instance;
    private static final String FILE_PATH = "./data/orders.txt";

    private OrderOperation() {}

    public static OrderOperation getInstance() {
        if (instance == null) { instance = new OrderOperation(); }
        return instance;
    }

    public String generateUniqueOrderId() {
        Random random = new Random();
        int uniqueNumber = 10000 + random.nextInt(90000); 
        return "o_" + uniqueNumber;
    }

    public boolean createAnOrder(String customerId, String productId, String createTime) {
        String orderId = generateUniqueOrderId();
        String orderTime = (createTime == null) ? LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy_HH:mm:ss")) : createTime;

        JSONObject orderJson = new JSONObject();
        orderJson.put("orderID", orderId);
        orderJson.put("userID", customerId);
        orderJson.put("proID", productId);
        orderJson.put("orderTime", orderTime);

        return writeOrderToFile(orderJson);
    }

    public boolean deleteOrder(String orderId) {
        List<String> updatedOrders = new ArrayList<>();
        boolean deleted = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                JSONObject orderJson = new JSONObject(line);
                if (!orderJson.getString("orderID").equals(orderId)) {
                    updatedOrders.add(line);
                } else {
                    deleted = true;
                }
            }
        } catch (IOException e) {
            System.out.println("Failed to delete order: " + e.getMessage());
        return false;
        }
        return deleted ? writeOrdersToFile(updatedOrders) : false;
    }

    public class OrderListResult {
        private List<Order> orders;
        private int currentPage;
        private int totalPages;

        public OrderListResult(List<Order> orders, int currentPage, int totalPages) {
            this.orders = orders;
            this.currentPage = currentPage;
            this.totalPages = totalPages;
        }

        public List<Order> getOrders() {
            return orders;
        }

        public int getCurrentPage() {
            return currentPage;
        }

        public int getTotalPages() {
            return totalPages;
        }
    }

    public OrderListResult getOrderList(String customerId, int pageNumber) {
        List<Order> orders = new ArrayList<>();
        int totalOrders = 0;
        int pageSize = 10;

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                JSONObject orderJson = new JSONObject(line);
                if (orderJson.getString("userID").equals(customerId)) {
                    totalOrders++;
                    if (totalOrders > (pageNumber - 1) * pageSize && orders.size() < pageSize) {
                        orders.add(new Order(
                            orderJson.getString("orderID"),
                            orderJson.getString("userID"),
                            orderJson.getString("proID"),
                            orderJson.getString("orderTime")
                        ));
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Failed to retrieve order list: " + e.getMessage());
        }

        return new OrderListResult(orders, pageNumber, (int) Math.ceil((double) totalOrders / pageSize));
    }

    public void generateTestOrderData() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (int i = 0; i < 10; i++) {
                String customerId = "customer_" + (i + 1);

                int orderCount = new Random().nextInt(151) + 50; 
                for (int j = 0; j < orderCount; j++) {
                    String productId = "product_" + new Random().nextInt(100);
                    String orderTime = LocalDateTime.now().minusMonths(new Random().nextInt(12))
                            .format(DateTimeFormatter.ofPattern("dd-MM-yyyy_HH:mm:ss"));

                    JSONObject orderJson = new JSONObject();
                    orderJson.put("orderID", generateUniqueOrderId());
                    orderJson.put("userID", customerId);
                    orderJson.put("proID", productId);
                    orderJson.put("orderTime", orderTime);

                    writer.write(orderJson.toString());
                    writer.newLine();
                }
            }
            System.out.println("Test order data generated successfully.");
        } catch (IOException e) {
            System.out.println("Failed to generate test order data: " + e.getMessage());
        }
    }

    public void deleteAllOrders() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            writer.write(""); // Clears file content
            System.out.println("All orders deleted successfully.");
        } catch (IOException e) {
            System.out.println("Failed to delete all orders: " + e.getMessage());
        }
    }

    private boolean writeOrderToFile(JSONObject order) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(order.toString());
            writer.newLine();
            return true;
        } catch (IOException e) {
            System.out.println("Failed to write order to file: " + e.getMessage());
            return false;
        }
    }

    private boolean writeOrdersToFile(List<String> orders) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (String order : orders) {
                writer.write(order);
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            System.out.println("Failed to update order file: " + e.getMessage());
            return false;
        }
    }

    private void saveChartAsImage(Chart chart, String fileName) {
        WritableImage image = chart.snapshot(new SnapshotParameters(), null);
        File file = new File("data/figure/" + fileName);

        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
            System.out.println("Chart saved: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("Failed to save chart image: " + e.getMessage());
        }
    }

    public void generateSingleCustomerConsumptionFigure(String customerId) {
    Map<String, Double> monthlyConsumption = new HashMap<>();

    try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
        String line;
        while ((line = reader.readLine()) != null) {
            JSONObject orderJson = new JSONObject(line);
            if (orderJson.getString("userID").equals(customerId)) {
                String orderMonth = orderJson.getString("orderTime").substring(3, 5); 
                double price = getProductPrice(orderJson.getString("proID"));
                monthlyConsumption.put(orderMonth, monthlyConsumption.getOrDefault(orderMonth, 0.0) + price);
            }
        }
    } catch (IOException e) {
        System.out.println("Failed to process customer consumption: " + e.getMessage());
        return;
    }

    CategoryAxis xAxis = new CategoryAxis();
    NumberAxis yAxis = new NumberAxis();
    BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
    barChart.setTitle("Customer Monthly Consumption");

    XYChart.Series<String, Number> series = new XYChart.Series<>();
    for (Map.Entry<String, Double> entry : monthlyConsumption.entrySet()) {
        series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
    }

    barChart.getData().add(series);
    saveChartAsImage(barChart, "customer_" + customerId + "_consumption.png");
}

public void generateAllCustomersConsumptionFigure() {
    Map<String, Map<String, Double>> customerMonthlyConsumption = new HashMap<>();

    try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
        String line;
        while ((line = reader.readLine()) != null) {
            JSONObject orderJson = new JSONObject(line);
            String customerId = orderJson.getString("userID");
            String orderMonth = orderJson.getString("orderTime").substring(3, 5); 
            double price = getProductPrice(orderJson.getString("proID"));

            customerMonthlyConsumption.putIfAbsent(customerId, new HashMap<>());
            customerMonthlyConsumption.get(customerId).put(orderMonth, customerMonthlyConsumption.get(customerId).getOrDefault(orderMonth, 0.0) + price);
        }
    } catch (IOException e) {
        System.out.println("Failed to process all customer consumption: " + e.getMessage());
        return;
    }

    CategoryAxis xAxis = new CategoryAxis();
    NumberAxis yAxis = new NumberAxis();
    StackedBarChart<String, Number> stackedBarChart = new StackedBarChart<>(xAxis, yAxis);
    stackedBarChart.setTitle("All Customers Monthly Consumption");

    for (Map.Entry<String, Map<String, Double>> customerEntry : customerMonthlyConsumption.entrySet()) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Customer " + customerEntry.getKey());
        for (Map.Entry<String, Double> monthEntry : customerEntry.getValue().entrySet()) {
            series.getData().add(new XYChart.Data<>(monthEntry.getKey(), monthEntry.getValue()));
        }
        stackedBarChart.getData().add(series);
    }

    saveChartAsImage(stackedBarChart, "all_customers_consumption.png");
}

public void generateAllTop10BestSellersFigure() {
    Map<String, Integer> productSales = new HashMap<>();

    try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
        String line;
        while ((line = reader.readLine()) != null) {
            JSONObject orderJson = new JSONObject(line);
            String productId = orderJson.getString("proID");
            productSales.put(productId, productSales.getOrDefault(productId, 0) + 1);
        }
    } catch (IOException e) {
        System.out.println("Failed to process top sellers: " + e.getMessage());
        return;
    }

    CategoryAxis xAxis = new CategoryAxis();
    NumberAxis yAxis = new NumberAxis();
    BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
    barChart.setTitle("Top 10 Best-Selling Products");

    XYChart.Series<String, Number> series = new XYChart.Series<>();
    productSales.entrySet().stream()
        .sorted(Map.Entry.<String, Integer>comparingByValue().reversed()) 
        .limit(10)
        .forEach(entry -> series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue())));

    barChart.getData().add(series);
    saveChartAsImage(barChart, "top_10_best_sellers.png");
}

private double getProductPrice(String productId) {
    try (BufferedReader reader = new BufferedReader(new FileReader("data/products.txt"))) {
        String line;
        while ((line = reader.readLine()) != null) {
            JSONObject productJson = new JSONObject(line);
            if (productJson.getString("proID").equals(productId)) {
                return productJson.getDouble("proCurrentPrice"); 
            }
        }
    } catch (IOException e) {
        System.out.println("Error retrieving product price: " + e.getMessage());
    }
    return 0.0; 
}
}
