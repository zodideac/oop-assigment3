import java.io.*;
import java.util.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ProductOperation {
  private static ProductOperation instance;
  private static final String FILE_PATH = "assignment/src/data/products.json";
  private static final int PAGE_SIZE = 10;
  private ProductOperation() {}

  public static ProductOperation getInstance() {
    if (instance == null) { instance = new ProductOperation(); }
            
    return instance;
  }

  public void extractProductsFromFiles(List<String> productFiles) {
    List<String> extractedProducts = new ArrayList<>();

    for (String filePath : productFiles) {
      try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
        String line;
        while ((line = reader.readLine()) != null) {
          extractedProducts.add(line);
        }
      } catch (IOException e) {
          System.err.println("Failed to read file: " + filePath + " - " + e.getMessage());
        }
    }

    writeProductsToFile(extractedProducts);
    System.out.println("Product extraction complete!");
  }

  public ProductListResult getProductList(int pageNumber) {
  List<Product> products = new ArrayList<>();
  int totalProducts = 0;

  try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
    String line;
    JSONParser parser = new JSONParser();

    while ((line = reader.readLine()) != null) {
      totalProducts++;
      JSONObject product = (JSONObject) parser.parse(line);
      if (products.size() < PAGE_SIZE && totalProducts > (pageNumber - 1) * PAGE_SIZE) {
        products.add(new Product(product));
      }
    }
  } catch (IOException | ParseException e) {
      System.err.println("Failed to retrieve products: " + e.getMessage());
    }

    return new ProductListResult(products, pageNumber, (int) Math.ceil((double) totalProducts / PAGE_SIZE));
  }

  public boolean deleteProduct(String productId) {
    List<String> updatedProducts = new ArrayList<>();
    boolean deleted = false;

    try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
      String line;
      JSONParser parser = new JSONParser();

      while ((line = reader.readLine()) != null) {
        JSONObject product = (JSONObject) parser.parse(line);
        if (!product.get("product_id").toString().equals(productId)) {
          updatedProducts.add(line);
        } else {
            deleted = true;
          }
      }
    } catch (IOException | ParseException e) {
        System.err.println("Failed to delete product: " + e.getMessage());
        return false;
      }

      return deleted && writeProductsToFile(updatedProducts);
  }

  public List<Product> getProductListByKeyword(String keyword) {
    List<Product> products = new ArrayList<>();
    try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
      String line;
      JSONParser parser = new JSONParser();

      while ((line = reader.readLine()) != null) {
        JSONObject product = (JSONObject) parser.parse(line);
        if (product.get("name").toString().toLowerCase().contains(keyword.toLowerCase())) {
          products.add(new Product(product));
        }
      }
    } catch (IOException | ParseException e) {
        System.err.println("Failed to search products: " + e.getMessage());
      }

      return products;
  }

   
  public Product getProductById(String productId) {
    try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
      String line;
      JSONParser parser = new JSONParser();

      while ((line = reader.readLine()) != null) {
        JSONObject product = (JSONObject) parser.parse(line);
        if (product.get("product_id").toString().equals(productId)) {
          return new Product(product);
        }
      }
    } catch (IOException | ParseException e) {
        System.err.println("Failed to retrieve product: " + e.getMessage());
      }

      return null;
  }

  //Bar chart
  public void generateCategoryFigure() {
    Map<String, Integer> categoryCounts = new HashMap<>();

    try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
      String line;
      JSONParser parser = new JSONParser();

      while ((line = reader.readLine()) != null) {
        JSONObject product = (JSONObject) parser.parse(line);
        String category = product.get("category").toString();
        categoryCounts.put(category, categoryCounts.getOrDefault(category, 0) + 1);
      }
    } catch (IOException | ParseException e) {
        System.err.println("Failed to process category data: " + e.getMessage());
      }

      ChartExporter.generateBarChart(categoryCounts, "Product Categories", "category_distribution.png");
  }

  //Pie chart
  public void generateDiscountFigure() {
    Map<String, Integer> discountGroups = new HashMap<>();
    discountGroups.put("<30%", 0);
    discountGroups.put("30-60%", 0);
    discountGroups.put(">60%", 0);

    try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
      String line;
      JSONParser parser = new JSONParser();

      while ((line = reader.readLine()) != null) {
        JSONObject product = (JSONObject) parser.parse(line);
        double discount = Double.parseDouble(product.get("discount").toString());

        if (discount < 30) {
          discountGroups.put("<30%", discountGroups.get("<30%") + 1);
        } else if (discount <= 60) {
            discountGroups.put("30-60%", discountGroups.get("30-60%") + 1);
          } else {
             discountGroups.put(">60%", discountGroups.get(">60%") + 1);
            }
      }
    } catch (IOException | ParseException e) {
        System.err.println("Failed to process discount data: " + e.getMessage());
      }

      ChartExporter.generatePieChart(discountGroups, "Product Discounts", "discount_distribution.png");
  }

  //Bar chart
  public void generateLikesCountFigure() {
    Map<String, Integer> likesCountMap = new HashMap<>();

    try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
      String line;
      JSONParser parser = new JSONParser();

      while ((line = reader.readLine()) != null) {
        JSONObject product = (JSONObject) parser.parse(line);
        String category = product.get("category").toString();
        int likes = Integer.parseInt(product.get("likes_count").toString());

        likesCountMap.put(category, likesCountMap.getOrDefault(category, 0) + likes);
      }
    } catch (IOException | ParseException e) {
        System.err.println("Failed to process likes count data: " + e.getMessage());
    }

    ChartExporter.generateBarChart(likesCountMap, "Likes Count by Category", "likes_count_distribution.png");
  }

  //Scatter chart
  public void generateDiscountLikesCountFigure() {
    Map<Double, Double> discountLikesMap = new HashMap<>();

    try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
      String line;
      JSONParser parser = new JSONParser();

      while ((line = reader.readLine()) != null) {
        JSONObject product = (JSONObject) parser.parse(line);
        double discount = Double.parseDouble(product.get("discount").toString());
        double likes = Double.parseDouble(product.get("likes_count").toString());

        discountLikesMap.put(discount, likes);
      }
    } catch (IOException | ParseException e) {
        System.err.println("Failed to process discount-likes data: " + e.getMessage());
    }

    ChartExporter.generateScatterChart(discountLikesMap, "Likes vs. Discount", "discount_likes_relationship.png");
  }

  public void deleteAllProducts() {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, false))) {
      writer.write(""); 
      System.out.println("All products deleted successfully!");
    } catch (IOException e) {
        System.err.println("Failed to delete all products: " + e.getMessage());
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
        System.err.println("Failed to write products to file: " + e.getMessage());
        return false;
    }
  }
}