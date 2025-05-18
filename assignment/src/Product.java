import org.json.simple.JSONObject;

public class Product {
    private String proId;
    private String proModel;
    private String proCategory;
    private String proName;
    private double proCurrentPrice;
    private double proRawPrice;
    private double proDiscount;
    private int proLikesCount;

    public Product(String proId, String proModel, String proCategory, 
                   String proName, double proCurrentPrice, double proRawPrice, 
                   double proDiscount, int proLikesCount) {
        this.proId = proId;
        this.proModel = proModel;
        this.proCategory = proCategory;
        this.proName = proName;
        this.proCurrentPrice = proCurrentPrice;
        this.proRawPrice = proRawPrice;
        this.proDiscount = proDiscount;
        this.proLikesCount = proLikesCount;
    }

    public Product(JSONObject json) {
        this.proId = json.get("pro_id").toString();
        this.proModel = json.get("pro_model").toString();
        this.proCategory = json.get("pro_category").toString();
        this.proName = json.get("pro_name").toString();
        this.proCurrentPrice = Double.parseDouble(json.get("pro_current_price").toString());
        this.proRawPrice = Double.parseDouble(json.get("pro_raw_price").toString());
        this.proDiscount = Double.parseDouble(json.get("pro_discount").toString());
        this.proLikesCount = Integer.parseInt(json.get("pro_likes_count").toString());
    }

    @Override
    public String toString() {
        return String.format("{\"pro_id\":\"%s\", \"pro_model\":\"%s\", \"pro_category\":\"%s\", " +
                             "\"pro_name\":\"%s\", \"pro_current_price\":\"%.2f\", \"pro_raw_price\":\"%.2f\", " +
                             "\"pro_discount\":\"%.2f\", \"pro_likes_count\":\"%d\"}", 
                             proId, proModel, proCategory, proName, proCurrentPrice, proRawPrice, 
                             proDiscount, proLikesCount);
    }

    // Getters
    public String getProId() { return proId; }
    public String getProModel() { return proModel; }
    public String getProCategory() { return proCategory; }
    public String getProName() { return proName; }
    public double getProCurrentPrice() { return proCurrentPrice; }
    public double getProRawPrice() { return proRawPrice; }
    public double getProDiscount() { return proDiscount; }
    public int getProLikesCount() { return proLikesCount; }
}