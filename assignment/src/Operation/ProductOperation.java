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

/**
 * The {@code ProductOperation} class handles various operations related to products,
 * including writing sample product data to a file, retrieving products with
 * pagination, deleting a product, and searching products by a keyword.
 * <p>
 * This class reads and writes product data to "assignment/data/products.txt" in JSON format.
 * It is implemented as a singleton to ensure a single point of product operation management.
 * </p>
 */
public class ProductOperation {

  /** Singleton instance of ProductOperation. */
  private static ProductOperation instance;

  /** JSON parser used for parsing product data from the file. */
  private JSONParser parser;

  /**
  * Private constructor to initialize the JSON parser.
  */
  private ProductOperation() {
    parser = new JSONParser();
  }

  /**
  * Retrieves the singleton instance of {@code ProductOperation}. If no instance exists,
  * a new one is created.
  *
  * @return the singleton instance of ProductOperation.
  */
  public static ProductOperation getInstance() {
    if (instance == null) { 
      instance = new ProductOperation();
    }
      return instance;
  }

  /**
  * Extracts sample product details and writes them to the products file.
  * <p>
  * This method creates a sample product record, converts it into a JSON string,
  * and writes it to "assignment/data/products.txt". The file is overwritten each time.
  * </p>
  */
  public void extractProductsFromFiles() {
    try (FileWriter writer = new FileWriter("assignment/data/products.txt", false)) {
      // Create a map containing sample product details.
      HashMap<String, Object> product1Details = new HashMap<String, Object>();
      product1Details.put("pro_id", "p_0001");
      product1Details.put("pro_model", "ModelX");
      product1Details.put("pro_category", "Electronics");
      product1Details.put("pro_name", "Smartphone");
      product1Details.put("pro_current_price", 699.99);
      product1Details.put("pro_raw_price", 899.99);
      product1Details.put("pro_discount", 20.0);
      product1Details.put("pro_likes_count", 150);

      // Convert the product details to a JSON object.
      JSONObject product1DetailsJSON = new JSONObject(product1Details);
      // Write the JSON string to the file, followed by a line separator.
      writer.write(product1DetailsJSON.toJSONString() + System.lineSeparator());

      System.out.println("Sample products extracted successfully.");
    } catch (IOException e) {
        System.err.println("Error writing to products file: " + e.getMessage());
      }
  }

  /**
  * Retrieves a paginated list of products from the products file.
  * <p>
  * This method reads all lines from "assignment/data/products.txt", parses each line as a JSON object
  * to create {@code Product} instances, and then paginates the results to return 10 products per page.
  * </p>
  *
  * @param pageNumber the page number to retrieve.
  * @return a {@code ProductListResult} encapsulating the products for the requested page,
  *         the current page number, and the total page count.
  */
  public ProductListResult getProductList(int pageNumber) {
    List<String> lines = new ArrayList<>();
    File file = new File("assignment/data/products.txt");

    // Read all lines from the products file.
    try (Scanner scanner = new Scanner(file)) {
      while (scanner.hasNextLine()) {
        lines.add(scanner.nextLine().trim());
      }
    } catch (IOException e) {
        System.err.println("Error reading products file: " + e.getMessage());
      }

    // Convert each non-empty line into a Product instance.
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

      // Calculate pagination details.
      int totalProducts = allProducts.size();
      int totalPages = (int) Math.ceil((double) totalProducts / 10);
      if (pageNumber < 1) pageNumber = 1;
      else if (pageNumber > totalPages) pageNumber = totalPages;
      int start = (pageNumber - 1) * 10;
      int end = Math.min(start + 10, totalProducts);
      List<Product> products = allProducts.subList(start, end);

      return new ProductListResult(products, pageNumber, totalPages);
  }

    /**
     * Deletes a product with the specified product ID from the products file.
     * <p>
     * The method reads all records from "assignment/data/products.txt", skips the record
     * matching the given product ID, and writes the remaining records back to the file.
     * </p>
     *
     * @param productId the ID of the product to delete.
     * @return true if the product was found and deleted; false otherwise.
     */
    public boolean deleteProduct(String productId) {
        List<String> lines = new ArrayList<>();
        File file = new File("assignment/data/products.txt");
        boolean found = false;

        // Read through each line of the file.
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                try {
                    JSONObject json = (JSONObject) parser.parse(line);
                    if (productId.equals(json.get("pro_id"))) {
                        found = true; // Skip this record.
                        continue;
                    }
                } catch (ParseException pe) {
                    // Ignore parse errors here; the line is added as is.
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

        // Write the remaining records back to the file.
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

    /**
     * Retrieves a list of products whose names contain the specified keyword.
     * <p>
     * The method reads each record from "assignment/data/products.txt" (in JSON format) and
     * filters products based on a case-insensitive search within the product name.
     * </p>
     *
     * @param keyword the keyword to search for.
     * @return a list of {@code Product} objects matching the keyword.
     */
    public List<Product> getProductListByKeyword(String keyword) {
        List<Product> matchedProducts = new ArrayList<>();
        File file = new File("assignment/data/products.txt");

        // Read the file and filter records by the product name.
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
                        
                        Product product = new Product(proId, proModel, proCategory, proName,
                                                      proCurrentPrice, proRawPrice, proDiscount, proLikesCount);
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

  
 /**
 * Retrieves a product by its unique identifier from the products file.
 *
 * @param productId the unique identifier of the product to retrieve.
 * @return a {@link Model.Product} object matching the given productId, or null if not found.
 */
public Product getProductById(String productId) {
    File file = new File("assignment/data/products.txt");
    try (Scanner scanner = new Scanner(file)) {
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) continue;
            try {
                // Parse the JSON record and check for a matching product ID.
                JSONObject json = (JSONObject) parser.parse(line);
                if (productId.equals(json.get("pro_id"))) {
                    String proModel = (String) json.get("pro_model");
                    String proCategory = (String) json.get("pro_category");
                    String proName = (String) json.get("pro_name");
                    double proCurrentPrice = Double.parseDouble(json.get("pro_current_price").toString());
                    double proRawPrice = Double.parseDouble(json.get("pro_raw_price").toString());
                    double proDiscount = Double.parseDouble(json.get("pro_discount").toString());
                    int proLikesCount = Integer.parseInt(json.get("pro_likes_count").toString());
                    // Return the constructed Product instance.
                    return new Product(productId, proModel, proCategory, proName, 
                                       proCurrentPrice, proRawPrice, proDiscount, proLikesCount);
                }
            } catch (ParseException pe) {
                System.err.println("Error parsing product record: " + pe.getMessage());
            }
        }
    } catch (IOException e) {
        System.err.println("Error reading products file: " + e.getMessage());
    }
    // If no matching product is found, return null.
    return null;
}

/**
 * Retrieves all products from the products file.
 *
 * @return a list of all {@link Model.Product} objects read from "assignment/data/products.txt".
 */
private List<Product> getAllProducts() {
    List<Product> products = new ArrayList<>();
    File file = new File("assignment/data/products.txt");
    try (Scanner scanner = new Scanner(file)) {
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) continue;
            try {
                // Parse the JSON line into a product record.
                JSONObject json = (JSONObject) parser.parse(line);
                String proId = (String) json.get("pro_id");
                String proModel = (String) json.get("pro_model");
                String proCategory = (String) json.get("pro_category");
                String proName = (String) json.get("pro_name");
                double proCurrentPrice = Double.parseDouble(json.get("pro_current_price").toString());
                double proRawPrice = Double.parseDouble(json.get("pro_raw_price").toString());
                double proDiscount = Double.parseDouble(json.get("pro_discount").toString());
                int proLikesCount = Integer.parseInt(json.get("pro_likes_count").toString());
                // Create a new Product object and add it to the list.
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

/**
 * Generates a bar chart that shows the number of products per category.
 * <p>
 * The method reads all product records, counts products in each category, creates a JavaFX 
 * BarChart with the results, takes a snapshot of the chart, and saves the image to 
 * "assignment/data/figure/category_chart.png".
 * </p>
 */
public void generateCategoryFigure() {
    new JFXPanel(); // Initializes JavaFX environment.
    Platform.runLater(() -> {
        List<Product> products = getAllProducts();
        Map<String, Integer> categoryCount = new HashMap<>();
        // Count the number of products per category.
        for (Product p : products) {
            String category = p.getProCategory();
            categoryCount.put(category, categoryCount.getOrDefault(category, 0) + 1);
        }
        
        // Sort the categories in descending order by count.
        List<Map.Entry<String, Integer>> sortedCategories = new ArrayList<>(categoryCount.entrySet());
        sortedCategories.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        
        // Set up the axes for the BarChart.
        final CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Category");
        final NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Number of Products");
        final BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Products by Category");
        
        // Populate the chart with series data.
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (Map.Entry<String, Integer> entry : sortedCategories) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }
        barChart.getData().add(series);
        
        // Create a Scene, take a snapshot of the chart, and save it to a PNG file.
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

/**
 * Generates a pie chart displaying the discount distribution among products.
 * <p>
 * Products are grouped into three categories:
 * <em>Discount < 30</em>, <em>30 <= Discount <= 60</em>, and <em>Discount > 60</em>.
 * The generated chart is saved as "assignment/data/figure/discount_chart.png".
 * </p>
 */
public void generateDiscountFigure() {
    new JFXPanel(); // Initializes JavaFX environment.
    Platform.runLater(() -> {
        List<Product> products = getAllProducts();
        int lessThan30 = 0, between30And60 = 0, greaterThan60 = 0;
        // Classify products based on discount.
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
        
        // Build the PieChart with the discount groups.
        PieChart pieChart = new PieChart();
        pieChart.setTitle("Discount Distribution");
        pieChart.getData().add(new PieChart.Data("Discount < 30", lessThan30));
        pieChart.getData().add(new PieChart.Data("30 <= Discount <= 60", between30And60));
        pieChart.getData().add(new PieChart.Data("Discount > 60", greaterThan60));
        
        // Set up the Scene, capture the chart as an image, and write the image to a file.
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

/**
 * Generates a bar chart showing the total likes count grouped by product category.
 * <p>
 * The method aggregates the likes for all products per category, sorts the categories in ascending
 * order based on total likes, creates a JavaFX BarChart, takes a snapshot, and saves the image to
 * "assignment/data/figure/likes_count_chart.png".
 * </p>
 */
public void generateLikesCountFigure() {
    new JFXPanel(); // Initializes JavaFX environment.
    Platform.runLater(() -> {
        List<Product> products = getAllProducts();
        Map<String, Integer> likesByCategory = new HashMap<>();
        // Sum up the likes count for each product category.
        for (Product p : products) {
            String category = p.getProCategory();
            int likes = p.getProLikesCount();
            likesByCategory.put(category, likesByCategory.getOrDefault(category, 0) + likes);
        }
        
        // Sort the categories in ascending order by total likes.
        List<Map.Entry<String, Integer>> sortedLikes = new ArrayList<>(likesByCategory.entrySet());
        sortedLikes.sort(Comparator.comparingInt(Map.Entry::getValue));
        
        // Configure axes and create a BarChart.
        final CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Category");
        final NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Total Likes");
        final BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Likes Count by Category");
        
        // Populate the chart with the sorted likes data.
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (Map.Entry<String, Integer> entry : sortedLikes) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }
        barChart.getData().add(series);
        
        // Create a scene with the chart, capture its snapshot, and save as an image.
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

  /**
 * Generates a scatter chart displaying the relationship
 * between product discount and likes count.
 * <p>
 * This method uses JavaFX to create a scatter chart with the X-axis representing
 * the discount and the Y-axis representing the likes count. It then captures a
 * snapshot of the chart and saves it as a PNG image in the specified directory.
 * </p>
 */
public void generateDiscountLikesCountFigure() {
    // Initialize the JavaFX environment.
    new JFXPanel();
    
    // Execute JavaFX-related code on the JavaFX Application Thread.
    Platform.runLater(() -> {
        // Retrieve all products from the data source.
        List<Product> products = getAllProducts();

        // Create a scatter chart with Number axes.
        final NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Discount");  // Label for X-axis.
        final NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Likes Count");  // Label for Y-axis.
        
        final ScatterChart<Number, Number> scatterChart = new ScatterChart<>(xAxis, yAxis);
        scatterChart.setTitle("Discount vs. Likes Count");

        // Create a data series and populate it with discount and likes count data.
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        for (Product p : products) {
            series.getData().add(new XYChart.Data<>(p.getProDiscount(), p.getProLikesCount()));
        }
        scatterChart.getData().add(series);

        // Create a scene to hold the chart and capture its snapshot.
        Scene scene = new Scene(scatterChart, 800, 600);
        WritableImage image = scene.snapshot(null);
        
        // Define the file to which the snapshot will be saved.
        File outputFile = new File("assignment/data/figure/discount_likes_scatter_chart.png");
        try {
            // Write the snapshot image to the file in PNG format.
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", outputFile);
            System.out.println("Discount vs. Likes scatter chart generated at: " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error saving scatter chart: " + e.getMessage());
        }
    });
}

/**
 * Deletes all product records by overwriting the products file with an empty string.
 * <p>
 * This method opens the "assignment/data/products.txt" file in write mode,
 * effectively removing all existing content and leaving an empty file.
 * </p>
 */
public void deleteAllProducts() {
    try (FileWriter writer = new FileWriter("assignment/data/products.txt", false)) {
        writer.write(""); // Overwrite the file with an empty string.
        System.out.println("All products deleted successfully.");
    } catch (IOException e) {
        System.err.println("Error deleting all products: " + e.getMessage());
    }
}

  /**
  * A helper class that encapsulates the result of a product list query with pagination.
  * <p>
  * It holds a list of {@link Product} objects along with the current page number and the total number
  * of pages available.
  * </p>
  */
  public static class ProductListResult {
    /** List of product records for the current page. */
    public List<Product> products;
    /** The current page number in the pagination. */
    public int currentPage;
    /** The total number of pages available. */
    public int totalPages;
    
    /**
     * Constructs a new {@code ProductListResult} with the specified values.
     *
     * @param products    the list of products for the current page
     * @param currentPage the current page number
     * @param totalPages  the total number of pages
     */
    public ProductListResult(List<Product> products, int currentPage, int totalPages) {
        this.products = products;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
    }
  }
}