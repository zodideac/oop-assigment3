package Model;

/**
 * The {@code Product} class represents an item available in the application.
 * It encapsulates product details such as a unique identifier, model, category, name,
 * pricing information (both current and raw prices), discount percentage, and the number
 * of likes. This class provides both a parameterized constructor for creating a product
 * with specific values and a default constructor that initializes the product with
 * placeholder values.
 */
public class Product {
    // Unique identifier for the product.
    private String proId;
    // Model identifier (e.g., iPhoneX, GalaxyS21).
    private String proModel;
    // The category to which the product belongs (e.g., Smartphone, Laptop).
    private String proCategory;
    // The name of the product.
    private String proName;
    // Current selling price of the product.
    private double proCurrentPrice;
    // Original price before any discount is applied.
    private double proRawPrice;
    // Discount percentage applied to the product.
    private double proDiscount;
    // The number of likes (popularity count) for the product.
    private int proLikesCount;

    /**
     * Constructs a new {@code Product} with the specified details.
     *
     * @param proId          the unique identifier for the product
     * @param proModel       the product model
     * @param proCategory    the category of the product
     * @param proName        the name of the product
     * @param proCurrentPrice the current selling price
     * @param proRawPrice    the original price before discount
     * @param proDiscount    the discount percentage
     * @param proLikesCount  the number of likes the product has received
     */
    public Product(String proId, String proModel, String proCategory, String proName, 
                   double proCurrentPrice, double proRawPrice, double proDiscount, int proLikesCount) {
        this.proId = proId;
        this.proModel = proModel;
        this.proCategory = proCategory;
        this.proName = proName;
        this.proCurrentPrice = proCurrentPrice;
        this.proRawPrice = proRawPrice;
        this.proDiscount = proDiscount;
        this.proLikesCount = proLikesCount;
    }

    /**
     * Returns the unique identifier of the product.
     *
     * @return the product id
     */
    public String getProId() {
        return proId;
    }

    /**
     * Sets the unique identifier of the product.
     *
     * @param proId the product id to set
     */
    public void setProId(String proId) {
        this.proId = proId;
    }

    /**
     * Returns the model of the product.
     *
     * @return the product model
     */
    public String getProModel() {
        return proModel;
    }

    /**
     * Sets the model of the product.
     *
     * @param proModel the product model to set
     */
    public void setProModel(String proModel) {
        this.proModel = proModel;
    }

    /**
     * Returns the category to which the product belongs.
     *
     * @return the product category
     */
    public String getProCategory() {
        return proCategory;
    }

    /**
     * Sets the category of the product.
     *
     * @param proCategory the product category to set
     */
    public void setProCategory(String proCategory) {
        this.proCategory = proCategory;
    }

    /**
     * Returns the name of the product.
     *
     * @return the product name
     */
    public String getProName() {
        return proName;
    }

    /**
     * Sets the name of the product.
     *
     * @param proName the product name to set
     */
    public void setProName(String proName) {
        this.proName = proName;
    }

    /**
     * Returns the current selling price of the product.
     *
     * @return the current price
     */
    public double getProCurrentPrice() {
        return proCurrentPrice;
    }

    /**
     * Sets the current selling price of the product.
     *
     * @param proCurrentPrice the current price to set
     */
    public void setProCurrentPrice(double proCurrentPrice) {
        this.proCurrentPrice = proCurrentPrice;
    }

    /**
     * Returns the original price of the product before any discount.
     *
     * @return the raw price
     */
    public double getProRawPrice() {
        return proRawPrice;
    }

    /**
     * Sets the original price of the product.
     *
     * @param proRawPrice the raw price to set
     */
    public void setProRawPrice(double proRawPrice) {
        this.proRawPrice = proRawPrice;
    }

    /**
     * Returns the discount percentage applied to the product.
     *
     * @return the discount percentage
     */
    public double getProDiscount() {
        return proDiscount;
    }

    /**
     * Sets the discount percentage for the product.
     *
     * @param proDiscount the discount percentage to set
     */
    public void setProDiscount(double proDiscount) {
        this.proDiscount = proDiscount;
    }

    /**
     * Returns the number of likes for the product.
     *
     * @return the like count
     */
    public int getProLikesCount() {
        return proLikesCount;
    }

    /**
     * Sets the like count for the product.
     *
     * @param proLikesCount the like count to set
     */
    public void setProLikesCount(int proLikesCount) {
        this.proLikesCount = proLikesCount;
    }

    /**
     * Default constructor that initializes the product with placeholder values.
     * <p>
     * This constructor is primarily useful for testing or when default values are acceptable.
     * </p>
     */
    public Product() {
        this.proId = "defaultId";
        this.proModel = "defaultModel";
        this.proCategory = "defaultCategory";
        this.proName = "defaultProduct";
        this.proCurrentPrice = 0.0;
        this.proRawPrice = 0.0;
        this.proDiscount = 0.0;
        this.proLikesCount = 0;
    }

    /**
     * Returns a JSON-formatted string representation of the product.
     * The output includes the product id, model, category, name, current price, raw price,
     * discount, and likes count.
     *
     * @return a JSON formatted string representing the product
     */
    @Override
    public String toString() {
        return String.format("{\"pro_id\":\"%s\",\"pro_model\":\"%s\",\"pro_category\":\"%s\"," +
                             "\"pro_name\":\"%s\",\"pro_current_price\":\"%.2f\",\"pro_raw_price\":\"%.2f\"," +
                             "\"pro_discount\":\"%.2f\",\"pro_likes_count\":\"%d\"}",
                             proId, proModel, proCategory, proName, 
                             proCurrentPrice, proRawPrice, proDiscount, proLikesCount);
    }
}