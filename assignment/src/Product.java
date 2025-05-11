public class Product{
    protected String proId;
    protected String proModel;
    protected String proCategory;
    protected String proName;
    private double proCurrentPrice;
    private double proRawPrice;
    private double proDiscount;
    private double proLikesDiscount;


    public Product(String proID, String proModel, String proCategory, String proName, double proCurrentPrice, double proRawPrice, double proDiscount, double proLikesDiscount) {
        this.proID = proID;
        this.proModel = proModel;
        this.proCategory = proCategory;
        this.proName = proName;
        this.proCurrentPrice = proCurrentPrice;
        this.proRawPrice = proRawPrice;
        this.proDiscount = proDiscount;
        this.proLikesDiscount = proLikesDiscount;
    }

    public String proID() {
        return proID;
    }

    public String proModel(){
        return proModel;
    }

    public String proCategory() {
        return proCategory;
    }

    public double proName() {
        return proName;
    }

    public double proCurrentPrice() {
        return proCurrentPrice;
    }
    public double proRawPrice() {        
        return proRawPrice;
    }
    public double proDiscount() {
        return proDiscount;
    }
    public double proLikesDiscount() {
        return proLikesDiscount;
    }
    public Product(){
        this.proID = proID;
        this.proModel = proModel;
        this.proCategory = proCategory;
        this.proName = proName;
        this.proCurrentPrice = proCurrentPrice;
        this.proRawPrice = proRawPrice;
        this.proDiscount = proDiscount;
        this.proLikesDiscount = proLikesDiscount;
    }

    @Override
    public String toString() {
        return "ProId: " + proID + "\nproModel: " + proModel + "\nproCategory: " + proCategory
                + "\nproName: " + proName + "\nproCurrentPrice:" + proCurrentPrice + "\nproRawPrice" + proRawPrice + "\nproDiscount" +proDiscount
                + "\nproLikesDiscount" + proLikesDiscount;
    }
}
