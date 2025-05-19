package Operation;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.ScatterChart;
import javafx.scene.image.WritableImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import Model.Product;

public class ProductOperation {
  private static ProductOperation instance;
  private JSONParser parser;

  private ProductOperation() {
    parser = new JSONParser();
  }
    
  public static ProductOperation getInstance() {
    if (instance == null) { instance = new ProductOperation(); }
      return instance;
  }
    
  public void extractProductsFromFiles() {
    try (FileWriter writer = new FileWriter("assignment/data/products.txt", false)) {
      HashMap<String,Object> product1Details = new HashMap<String,Object>();
      product1Details.put("pro_id", "p_0001");
      product1Details.put("pro_model", "ModelX");
      product1Details.put("pro_category", "Electronics");
      product1Details.put("pro_name", "Smartphone");
      product1Details.put("pro_current_price", 699.99);
      product1Details.put("pro_raw_price", 899.99);
      product1Details.put("pro_discount", 20.0);
      product1Details.put("pro_likes_count", 150);
      JSONObject product1DetailsJSON = new JSONObject(product1Details);
      writer.write(product1DetailsJSON.toJSONString() + System.lineSeparator());
            
      System.out.println("Sample products extracted successfully.");
    } catch (IOException e) {
        System.err.println("Error writing to products file: " + e.getMessage());
      }
  }
  
  public ProductListResult getProductList(int pageNumber) {
    List<String> lines = new ArrayList<>();
    File file = new File("assignment/data/products.txt");
        
    try (Scanner scanner = new Scanner(file)) {
      while (scanner.hasNextLine()) {
        lines.add(scanner.nextLine().trim());
      }
    } catch (IOException e) {
        System.err.println("Error reading products file: " + e.getMessage());
      }
        
    List<Product> allProducts = new ArrayList<>();
    for (String line : lines) {
      if (line.isEmpty()) continue;
        try {
          JSONObject json = (JSONObject) parser.parse(line);
          String proId = (String) json.get("pro_id");
          String proModel = (String) json.get("pro_model");
          String proCategory = (String) json.get("pro_category");
          String proName = (String) json.get("pro_name");
          double proCurrentPrice = Double.parseDouble(json.get("pro_current_price").toString());
          double proRawPrice = Double.parseDouble(json.get("pro_raw_price").toString());
          double proDiscount = Double.parseDouble(json.get("pro_discount").toString());
          int proLikesCount = Integer.parseInt(json.get("pro_likes_count").toString());
                
          Product product = new Product(proId, proModel, proCategory, proName, proCurrentPrice, proRawPrice, proDiscount, proLikesCount);
            allProducts.add(product);
          } catch (ParseException pe) {
              System.err.println("Error parsing product JSON: " + pe.getMessage());
            } catch (NumberFormatException nfe) {
                System.err.println("Error converting numerical values: " + nfe.getMessage());
            }
        }
        
        int totalProducts = allProducts.size();
        int totalPages = (int) Math.ceil((double) totalProducts / 10);
        if (pageNumber < 1) pageNumber = 1;
        else if (pageNumber > totalPages) pageNumber = totalPages;
        int start = (pageNumber - 1) * 10;
        int end = Math.min(start + 10, totalProducts);
        List<Product> products = allProducts.subList(start, end);
        
        return new ProductListResult(products, pageNumber, totalPages);
    }
    
  public boolean deleteProduct(String productId) {
    List<String> lines = new ArrayList<>();
    File file = new File("assignment/data/products.txt");
    boolean found = false;
        
    try (Scanner scanner = new Scanner(file)) {
      while (scanner.hasNextLine()) {
        String line = scanner.nextLine();
          try {
            JSONObject json = (JSONObject) parser.parse(line);
            if (productId.equals(json.get("pro_id"))) {
              found = true; // Skip this product record.
              continue;
            }
          } catch (ParseException pe) {
                    
            }
              lines.add(line);
        }
    } catch (IOException e) {
        System.err.println("Error reading products file: " + e.getMessage());
        return false;
      }
        
      if (!found) {
        System.out.println("Product with id " + productId + " not found.");
        return false;
      }
        
      try (FileWriter writer = new FileWriter("assignment/data/products.txt", false)) {
        for (String line : lines) {
          writer.write(line + System.lineSeparator());
        }
      } catch (IOException e) {
          System.err.println("Error writing products file: " + e.getMessage());
          return false;
        }
        
      return true;
  }
    
  public List<Product> getProductListByKeyword(String keyword) {
    List<Product> matchedProducts = new ArrayList<>();
    File file = new File("assignment/data/products.txt");
        
    try (Scanner scanner = new Scanner(file)) {
      while (scanner.hasNextLine()) {
        String line = scanner.nextLine().trim();
        if (line.isEmpty()) continue;
        try {
          JSONObject json = (JSONObject) parser.parse(line);
          String proName = (String) json.get("pro_name");
          if (proName.toLowerCase().contains(keyword.toLowerCase())) {
            String proId = (String) json.get("pro_id");
            String proModel = (String) json.get("pro_model");
            String proCategory = (String) json.get("pro_category");
            double proCurrentPrice = Double.parseDouble(json.get("pro_current_price").toString());
            double proRawPrice = Double.parseDouble(json.get("pro_raw_price").toString());
            double proDiscount = Double.parseDouble(json.get("pro_discount").toString());
            int proLikesCount = Integer.parseInt(json.get("pro_likes_count").toString());
            Product product = new Product(proId, proModel, proCategory, proName, proCurrentPrice, proRawPrice, proDiscount, proLikesCount);
              matchedProducts.add(product);
            }
          } catch (ParseException | NumberFormatException ex) {
              System.err.println("Error processing product record: " + ex.getMessage());
            }
      }
    } catch (IOException e) {
        System.err.println("Error reading products file: " + e.getMessage());
      }
        
    return matchedProducts;
  }
    
  public Product getProductById(String productId) {
    File file = new File("assignment/data/products.txt");
    try (Scanner scanner = new Scanner(file)) {
      while (scanner.hasNextLine()) {
        String line = scanner.nextLine().trim();
        if (line.isEmpty()) continue;
        try {
          JSONObject json = (JSONObject) parser.parse(line);
          if (productId.equals(json.get("pro_id"))) {
            String proModel = (String) json.get("pro_model");
            String proCategory = (String) json.get("pro_category");
            String proName = (String) json.get("pro_name");
            double proCurrentPrice = Double.parseDouble(json.get("pro_current_price").toString());
            double proRawPrice = Double.parseDouble(json.get("pro_raw_price").toString());
            double proDiscount = Double.parseDouble(json.get("pro_discount").toString());
            int proLikesCount = Integer.parseInt(json.get("pro_likes_count").toString());
            return new Product(productId, proModel, proCategory, proName, proCurrentPrice, proRawPrice, proDiscount, proLikesCount);
          }
        } catch (ParseException pe) {
            System.err.println("Error parsing product record: " + pe.getMessage());
          }
      }
    } catch (IOException e) {
       System.err.println("Error reading products file: " + e.getMessage());
      }

    return null;
  }

  private List<Product> getAllProducts() {
    List<Product> products = new ArrayList<>();
    File file = new File("assignment/data/products.txt");
    try (Scanner scanner = new Scanner(file)) {
      while (scanner.hasNextLine()) {
        String line = scanner.nextLine().trim();
        if (line.isEmpty()) continue;
        try {
          JSONObject json = (JSONObject) parser.parse(line);
          String proId = (String) json.get("pro_id");
          String proModel = (String) json.get("pro_model");
          String proCategory = (String) json.get("pro_category");
          String proName = (String) json.get("pro_name");
          double proCurrentPrice = Double.parseDouble(json.get("pro_current_price").toString());
          double proRawPrice = Double.parseDouble(json.get("pro_raw_price").toString());
          double proDiscount = Double.parseDouble(json.get("pro_discount").toString());
          int proLikesCount = Integer.parseInt(json.get("pro_likes_count").toString());
          products.add(new Product(proId, proModel, proCategory, proName, 
              proCurrentPrice, proRawPrice, proDiscount, proLikesCount));
          } catch (ParseException pe) {
              System.err.println("Error parsing product JSON: " + pe.getMessage());
          }
      }
    } catch (IOException e) {
        System.err.println("Error reading products file: " + e.getMessage());
    }
    return products;
  }


  public void generateCategoryFigure() {
    new JFXPanel();
    Platform.runLater(() -> {
      List<Product> products = getAllProducts();
      Map<String, Integer> categoryCount = new HashMap<>();
      for (Product p : products) {
        // Assumes Product has a getProCategory() method.
        String category = p.getProCategory();
        categoryCount.put(category, categoryCount.getOrDefault(category, 0) + 1);
      }
        
      // Sort categories in descending order by count.
      List<Map.Entry<String, Integer>> sortedCategories = new ArrayList<>(categoryCount.entrySet());
      sortedCategories.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        
      // Create a BarChart.
      final CategoryAxis xAxis = new CategoryAxis();
      xAxis.setLabel("Category");
      final NumberAxis yAxis = new NumberAxis();
      yAxis.setLabel("Number of Products");
      final BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
      barChart.setTitle("Products by Category");
        
      XYChart.Series<String, Number> series = new XYChart.Series<>();
      for (Map.Entry<String, Integer> entry : sortedCategories) {
        series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
      }
      barChart.getData().add(series);
        
      // Create a Scene to hold the chart and take a snapshot.
      Scene scene = new Scene(barChart, 800, 600);
      WritableImage image = scene.snapshot(null);
      File outputFile = new File("assignment/data/figure/category_chart.png");
      try {
        ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", outputFile);
        System.out.println("Category chart generated at: " + outputFile.getAbsolutePath());
      } catch (IOException e) {
          System.err.println("Error saving category chart: " + e.getMessage());
        }
    });
  }

  public void generateDiscountFigure() {
    new JFXPanel();
    Platform.runLater(() -> {
      List<Product> products = getAllProducts();
      int lessThan30 = 0, between30And60 = 0, greaterThan60 = 0;
      for (Product p : products) {
        double discount = p.getProDiscount();
        if (discount < 30) {
          lessThan30++;
        } else if (discount <= 60) {
            between30And60++;
          } else {
              greaterThan60++;
            }
      }
        
    PieChart pieChart = new PieChart();
    pieChart.setTitle("Discount Distribution");
    pieChart.getData().add(new PieChart.Data("Discount < 30", lessThan30));
    pieChart.getData().add(new PieChart.Data("30 <= Discount <= 60", between30And60));
    pieChart.getData().add(new PieChart.Data("Discount > 60", greaterThan60));
        
    // Create a Scene and capture the chart as an image.
    Scene scene = new Scene(pieChart, 800, 600);
    WritableImage image = scene.snapshot(null);
    File outputFile = new File("assignment/data/figure/discount_chart.png");
    try {
      ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", outputFile);
      System.out.println("Discount chart generated at: " + outputFile.getAbsolutePath());
    } catch (IOException e) {
        System.err.println("Error saving discount chart: " + e.getMessage());
      }
    });
  }

  public void generateLikesCountFigure() {
    new JFXPanel();
    Platform.runLater(() -> {
    List<Product> products = getAllProducts();
    Map<String, Integer> likesByCategory = new HashMap<>();
    for (Product p : products) {
      String category = p.getProCategory();
      int likes = p.getProLikesCount();
      likesByCategory.put(category, likesByCategory.getOrDefault(category, 0) + likes);
    }
        
    // Sort categories in ascending order based on likes.
    List<Map.Entry<String, Integer>> sortedLikes = new ArrayList<>(likesByCategory.entrySet());
    sortedLikes.sort(Comparator.comparingInt(Map.Entry::getValue));
        
    final CategoryAxis xAxis = new CategoryAxis();
    xAxis.setLabel("Category");
    final NumberAxis yAxis = new NumberAxis();
    yAxis.setLabel("Total Likes");
    final BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
    barChart.setTitle("Likes Count by Category");
        
    XYChart.Series<String, Number> series = new XYChart.Series<>();
    for (Map.Entry<String, Integer> entry : sortedLikes) {
      series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
    }
    barChart.getData().add(series);
        
    Scene scene = new Scene(barChart, 800, 600);
    WritableImage image = scene.snapshot(null);
    File outputFile = new File("assignment/data/figure/likes_count_chart.png");
    try {
      ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", outputFile);
      System.out.println("Likes count chart generated at: " + outputFile.getAbsolutePath());
    } catch (IOException e) {
        System.err.println("Error saving likes count chart: " + e.getMessage());
      }
    });
  }

  public void generateDiscountLikesCountFigure() {
    new JFXPanel();
    Platform.runLater(() -> {
    List<Product> products = getAllProducts();
        
    // Create a scatter chart.
    final NumberAxis xAxis = new NumberAxis();
    xAxis.setLabel("Discount");
    final NumberAxis yAxis = new NumberAxis();
    yAxis.setLabel("Likes Count");
    final ScatterChart<Number, Number> scatterChart = new ScatterChart<>(xAxis, yAxis);
    scatterChart.setTitle("Discount vs. Likes Count");
        
    XYChart.Series<Number, Number> series = new XYChart.Series<>();
    for (Product p : products) {
      series.getData().add(new XYChart.Data<>(p.getProDiscount(), p.getProLikesCount()));
    }
    scatterChart.getData().add(series);
        
    Scene scene = new Scene(scatterChart, 800, 600);
    WritableImage image = scene.snapshot(null);
    File outputFile = new File("assignment/data/figure/discount_likes_scatter_chart.png");
    try {
      ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", outputFile);
      System.out.println("Discount vs. Likes scatter chart generated at: " + outputFile.getAbsolutePath());
    } catch (IOException e) {
        System.err.println("Error saving scatter chart: " + e.getMessage());
      }
    });
  }

  public void deleteAllProducts() {
    try (FileWriter writer = new FileWriter("assignment/data/products.txt", false)) {
    writer.write(""); // Overwrite with an empty file.
      System.out.println("All products deleted successfully.");
    } catch (IOException e) {
        System.err.println("Error deleting all products: " + e.getMessage());
      }
  }
    
  public static class ProductListResult {
    public List<Product> products;
    public int currentPage;
    public int totalPages;
        
    public ProductListResult(List<Product> products, int currentPage, int totalPages) {
      this.products = products;
      this.currentPage = currentPage;
      this.totalPages = totalPages;
    }
  }
}