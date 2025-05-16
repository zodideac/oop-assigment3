import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;
import org.json.*;
import javafx.scene.chart.*;
import javafx.scene.image.WritableImage;
import javafx.scene.SnapshotParameters;
import javafx.embed.swing.SwingFXUtils;



public class ProductOperation {
  private static ProductOperation instance;
  private static final String FILE_PATH = "data/products.txt";

  private ProductOperation() {}

  public static ProductOperation getInstance() {
    if (instance == null) { instance = new ProductOperation(); }

    return instance;
  }

  public void extractProductsFromFiles() {
    File sourceFile = new File("data/source_products.txt");

    try (BufferedReader reader = new BufferedReader(new FileReader(sourceFile));
      BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
        String line;

        while ((line = reader.readLine()) != null) {
          writer.write(line);
          writer.newLine();
        }

      System.out.println("Products extracted successfully.");
    } catch (IOException e) {
        System.out.println("Failed to extract products: " + e.getMessage());
      }
  }

  public class ProductListResult {
    private List<Product> products;
    private int currentPage;
    private int totalPages;

    public ProductListResult(List<Product> products, int currentPage, int totalPages) {
      this.products = products;
      this.currentPage = currentPage;
      this.totalPages = totalPages;
    }

    public List<Product> getProducts() {
      return products;
    }

    public int getCurrentPage() {
      return currentPage;
    }

    public int getTotalPages() {
      return totalPages;
    }
  }

  public ProductListResult getProductList(int pageNumber) {
    List<Product> products = new ArrayList<>();
    int totalProducts = 0;
    int pageSize = 10;

    try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
      String line;
      while ((line = reader.readLine()) != null) {
        totalProducts++;

        if (totalProducts > (pageNumber - 1) * pageSize && products.size() < pageSize) {
          JSONObject productJson = new JSONObject(line);
          products.add(new Product(
              productJson.getString("proID"),
              productJson.getString("proModel"),
              productJson.getString("proCategory"),
              productJson.getString("proName"),
              productJson.getDouble("proCurrentPrice"),
              productJson.getDouble("proRawPrice"),
              productJson.getDouble("proDiscount"),
              productJson.getDouble("proLikesDiscount")
              ));
        }
      }
    } catch (IOException e) {
        System.out.println("Error reading product list: " + e.getMessage());
      }

        return new ProductListResult(products, pageNumber, (int) Math.ceil((double) totalProducts / pageSize));
  }

  public boolean deleteProduct(String productId) {
    List<String> updatedProducts = new ArrayList<>();
    boolean deleted = false;

    try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
      String line;
      while ((line = reader.readLine()) != null) {
        JSONObject productJson = new JSONObject(line);
        if (!productJson.getString("proID").equals(productId)) {
          updatedProducts.add(line);
        } else {
            deleted = true;
          }
      }
    } catch (IOException e) {
        System.out.println("Failed to delete product: " + e.getMessage());
        return false;
      }

        return deleted ? writeProductsToFile(updatedProducts) : false;
    }

  public List<Product> getProductListByKeyword(String keyword) {
    List<Product> matchingProducts = new ArrayList<>();

    try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
      String line;

      while ((line = reader.readLine()) != null) {
        JSONObject productJson = new JSONObject(line);

        if (productJson.getString("proName").toLowerCase().contains(keyword.toLowerCase())) {
          matchingProducts.add(new Product(
            productJson.getString("proID"),
            productJson.getString("proModel"),
            productJson.getString("proCategory"),
            productJson.getString("proName"),
            productJson.getDouble("proCurrentPrice"),
            productJson.getDouble("proRawPrice"),
            productJson.getDouble("proDiscount"),
            productJson.getDouble("proLikesDiscount")
            ));
        }
      }
    } catch (IOException e) {
        System.out.println("Failed to search products: " + e.getMessage());
      }

    return matchingProducts;
  }

  public Product getProductById(String productId) {
    try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
      String line;

      while ((line = reader.readLine()) != null) {
        JSONObject productJson = new JSONObject(line);
        if (productJson.getString("proID").equals(productId)) {
          return new Product(
              productJson.getString("proID"),
              productJson.getString("proModel"),
              productJson.getString("proCategory"),
              productJson.getString("proName"),
              productJson.getDouble("proCurrentPrice"),
              productJson.getDouble("proRawPrice"),
              productJson.getDouble("proDiscount"),
              productJson.getDouble("proLikesDiscount")
              );
        }
      }
    } catch (IOException e) {
        System.out.println("Failed to retrieve product: " + e.getMessage());
      }

    return null;
  }

  public void deleteAllProducts() {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
      writer.write(""); 
      System.out.println("All products deleted successfully.");
    } catch (IOException e) {
        System.out.println("Failed to delete all products: " + e.getMessage());
      }
  }

  private boolean writeProductsToFile(List<String> products) {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
      for (String product : products) {
        writer.write(product);
        writer.newLine();
      }
        return true;

    } catch (IOException e) {
        System.out.println("Failed to update product file: " + e.getMessage());
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

  public void generateCategoryFigure() {
    Map<String, Integer> categoryCount = new HashMap<>();

    try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
      String line;
      while ((line = reader.readLine()) != null) {
        JSONObject productJson = new JSONObject(line);
        String category = productJson.getString("proCategory");
        categoryCount.put(category, categoryCount.getOrDefault(category, 0) + 1);
      }
    } catch (IOException e) {
        System.out.println("Error processing category data: " + e.getMessage());
      return;
    }

    CategoryAxis xAxis = new CategoryAxis();
    NumberAxis yAxis = new NumberAxis();
    BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
    barChart.setTitle("Product Categories (Descending)");

    XYChart.Series<String, Number> series = new XYChart.Series<>();
    categoryCount.entrySet().stream()
      .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
      .forEach(entry -> series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue())));

    barChart.getData().add(series);
    saveChartAsImage(barChart, "category_chart.png");
  }

  public void generateDiscountFigure() {
    int lowDiscount = 0, midDiscount = 0, highDiscount = 0;

    try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
      String line;

      while ((line = reader.readLine()) != null) {
        JSONObject productJson = new JSONObject(line);
        double discount = productJson.getDouble("proDiscount");
        if (discount < 30) lowDiscount++;
        else if (discount <= 60) midDiscount++;
        else highDiscount++;
      }
    } catch (IOException e) {
        System.out.println("Failed to process discount data: " + e.getMessage());
      return;
    }

    PieChart pieChart = new PieChart();
    pieChart.getData().addAll(
      new PieChart.Data("Less than 30%", lowDiscount),
      new PieChart.Data("30% - 60%", midDiscount),
      new PieChart.Data("Above 60%", highDiscount)
    );

    pieChart.setTitle("Discount Distribution");
    saveChartAsImage(pieChart, "discount_chart.png");
 }

 public void generateLikesCountFigure() {
    Map<String, Double> categoryLikes = new HashMap<>();

    try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
      String line;
      while ((line = reader.readLine()) != null) {
        JSONObject productJson = new JSONObject(line);
        String category = productJson.getString("proCategory");
        double likes = productJson.getDouble("proLikesDiscount");
        categoryLikes.put(category, categoryLikes.getOrDefault(category, 0.0) + likes);
      }
    } catch (IOException e) {
        System.out.println("Failed to process likes count: " + e.getMessage());
      return;
    }

    CategoryAxis xAxis = new CategoryAxis();
    NumberAxis yAxis = new NumberAxis();
    BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
    barChart.setTitle("Total Likes per Category (Ascending)");

    XYChart.Series<String, Number> series = new XYChart.Series<>();
    categoryLikes.entrySet().stream()
      .sorted(Map.Entry.comparingByValue()) // Ascending order
      .forEach(entry -> series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue())));

    barChart.getData().add(series);
    saveChartAsImage(barChart, "likes_chart.png");
 }

 public void generateDiscountLikesCountFigure() {
    ScatterChart<Number, Number> scatterChart = new ScatterChart<>(new NumberAxis(), new NumberAxis());
    scatterChart.setTitle("Likes Count vs Discount");

    XYChart.Series<Number, Number> series = new XYChart.Series<>();

    try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
      String line;
      while ((line = reader.readLine()) != null) {
        JSONObject productJson = new JSONObject(line);
        double discount = productJson.getDouble("proDiscount");
        double likes = productJson.getDouble("proLikesDiscount");
        series.getData().add(new XYChart.Data<>(discount, likes));
      }
    } catch (IOException e) {
        System.out.println("Failed to process discount-likes data: " + e.getMessage());
      return;
    }

    scatterChart.getData().add(series);
    saveChartAsImage(scatterChart, "discount_likes_chart.png");
 }
}
