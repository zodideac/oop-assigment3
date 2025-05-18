import java.io.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.stage.Stage;


public class OrderOperation {
    private static OrderOperation instance;
    private static final String FILE_PATH = "assignment/src/data/orders.json";
    private static final int PAGE_SIZE = 10; 
    private OrderOperation() {}

    public static OrderOperation getInstance() {
        if (instance == null) { instance = new OrderOperation(); }
            
        return instance;
    }

    public String generateUniqueOrderId() {
        return "o_" + (10000 + new Random().nextInt(90000)); 
    }

    public boolean createAnOrder(String customerId, String productId, String createTime) {
        if (customerId == null || productId == null) {
            return false;
        }

        JSONObject newOrderJSON = new JSONObject();
        HashMap<String,Object> newOrder = new HashMap<String,Object>();
        newOrder.put("order_id", generateUniqueOrderId());
        newOrder.put("customer_id", customerId);
        newOrder.put("product_id", productId);
        newOrder.put("order_time", (createTime != null) ? createTime : LocalDateTime.now().toString());

        return writeOrderToFile(newOrderJSON);
    }

    public boolean deleteOrder(String orderId) {
        List<String> updatedOrders = new ArrayList<>();
        boolean deleted = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            JSONParser parser = new JSONParser();

            while ((line = reader.readLine()) != null) {
                JSONObject order = (JSONObject) parser.parse(line);
                if (!order.get("order_id").toString().equals(orderId)) {
                    updatedOrders.add(line);
                } else {
                    deleted = true;
                }
            }
        } catch (IOException | ParseException e) {
            System.err.println("Failed to delete order: " + e.getMessage());
            e.printStackTrace();
            return false;
        }

        return deleted && writeOrdersToFile(updatedOrders);
    }

    public OrderListResult getOrderList(String customerId, int pageNumber) {
        List<Order> orders = new ArrayList<>();
        int totalOrders = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            JSONParser parser = new JSONParser();

            while ((line = reader.readLine()) != null) {
                totalOrders++;
                JSONObject order = (JSONObject) parser.parse(line);

                if (customerId.equals("all") || order.get("customer_id").toString().equals(customerId)) {
                    if (orders.size() < PAGE_SIZE && totalOrders > (pageNumber - 1) * PAGE_SIZE) {
                        orders.add(new Order(
                            order.get("order_id").toString(),
                            order.get("customer_id").toString(),
                            order.get("product_id").toString(),
                            order.get("order_time").toString()
                        ));
                    }
                }
            }
        } catch (IOException | ParseException e) {
            System.err.println("Failed to retrieve orders: " + e.getMessage());
            e.printStackTrace();
        }

        return new OrderListResult(orders, pageNumber, (int) Math.ceil((double) totalOrders / PAGE_SIZE));
    }

    public void generateTestOrderData() {
        for (int i = 1; i <= 10; i++) {
            String customerId = "C_" + i;
            for (int j = 0; j < 50 + new Random().nextInt(151); j++) {
                String productId = "P_" + new Random().nextInt(100);
                String orderTime = LocalDateTime.now().minusMonths(new Random().nextInt(12)).format(DateTimeFormatter.ISO_DATE_TIME);
                createAnOrder(customerId, productId, orderTime);
            }
        }
        System.out.println("Test order data generated successfully!");
    }

    //Line chart
    public void generateSingleCustomerConsumptionFigure(String customerId) {
        Map<Month, Double> monthlyConsumption = new HashMap<>();
        for (Month month : Month.values()) {
            monthlyConsumption.put(month, 0.0);
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            JSONParser parser = new JSONParser();
            String line;
            
            while ((line = reader.readLine()) != null) {
                JSONObject order = (JSONObject) parser.parse(line);
                if (order.get("customer_id").toString().equals(customerId)) {
                    LocalDateTime orderDate = LocalDateTime.parse(order.get("order_time").toString(), DateTimeFormatter.ISO_DATE_TIME);
                    double price = Double.parseDouble(order.get("order_price").toString());
                    monthlyConsumption.put(orderDate.getMonth(), monthlyConsumption.get(orderDate.getMonth()) + price);
                }
            }
        } catch (IOException | ParseException e) {
            System.err.println("Failed to generate consumption figure: " + e.getMessage());
            e.printStackTrace();
        }

        showLineChart("Customer Consumption", "Months", "Total Spent ($)", monthlyConsumption);
    }

    //Line chart
    public void generateAllCustomersConsumptionFigure() {
        Map<Month, Double> monthlyConsumption = new HashMap<>();
        for (Month month : Month.values()) {
            monthlyConsumption.put(month, 0.0);
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            JSONParser parser = new JSONParser();
            String line;
            
            while ((line = reader.readLine()) != null) {
                JSONObject order = (JSONObject) parser.parse(line);
                LocalDateTime orderDate = LocalDateTime.parse(order.get("order_time").toString(), DateTimeFormatter.ISO_DATE_TIME);
                double price = Double.parseDouble(order.get("order_price").toString());
                monthlyConsumption.put(orderDate.getMonth(), monthlyConsumption.get(orderDate.getMonth()) + price);
            }
        } catch (IOException | ParseException e) {
            System.err.println("Failed to generate consumption figure: " + e.getMessage());
            e.printStackTrace();
        }

        showLineChart("All Customers Consumption", "Months", "Total Revenue ($)", monthlyConsumption);
    }

    //Bar chart
    public void generateAllTop10BestSellersFigure() {
        Map<String, Integer> productSales = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            JSONParser parser = new JSONParser();
            String line;
            
            while ((line = reader.readLine()) != null) {
                JSONObject order = (JSONObject) parser.parse(line);
                String productId = order.get("product_id").toString();
                productSales.put(productId, productSales.getOrDefault(productId, 0) + 1);
            }
        } catch (IOException | ParseException e) {
            System.err.println("Failed to generate top sellers figure: " + e.getMessage());
            e.printStackTrace();
        }

        List<Map.Entry<String, Integer>> sortedProducts = new ArrayList<>(productSales.entrySet());
        sortedProducts.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        Map<String, Integer> top10 = new LinkedHashMap<>();
        for (int i = 0; i < Math.min(10, sortedProducts.size()); i++) {
            top10.put(sortedProducts.get(i).getKey(), sortedProducts.get(i).getValue());
        }

        showBarChart("Top 10 Best-Selling Products", "Products", "Sales Count", top10);
    }

    private void showLineChart(String title, String xAxisLabel, String yAxisLabel, Map<Month, Double> data) {
        Stage stage = new Stage();
        stage.setTitle(title);

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel(xAxisLabel);

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel(yAxisLabel);

        LineChart<String, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle(title);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(title);
        for (Map.Entry<Month, Double> entry : data.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey().name(), entry.getValue()));
        }

        chart.getData().add(series);
        Scene scene = new Scene(chart, 800, 600);
        stage.setScene(scene);
        stage.show();
    }

    private void showBarChart(String title, String xAxisLabel, String yAxisLabel, Map<String, Integer> data) {
        Stage stage = new Stage();
        stage.setTitle(title);

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel(xAxisLabel);

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel(yAxisLabel);

        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle(title);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(title);
        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        chart.getData().add(series);
        Scene scene = new Scene(chart, 800, 600);
        stage.setScene(scene);
        stage.show();
    }

    public void deleteAllOrders() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            writer.write(""); 
        } catch (IOException e) {
            System.err.println("Failed to delete all orders: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean writeOrderToFile(JSONObject order) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(order.toJSONString());
            writer.newLine();
            return true;
        } catch (IOException e) {
            System.err.println("Failed to write order: " + e.getMessage());
            e.printStackTrace();
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
            System.err.println("Failed to update orders file: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}