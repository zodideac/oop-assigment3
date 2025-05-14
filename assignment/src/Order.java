import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Order {
    protected String orderID;
    protected String userID;
    protected String proID;
    protected String ordertime;


    public Order(String userID, String userName, String userPassword, String userRegisterTime, String userRole) {
        this.oderID = orderID;
        this.userID = userID;
        this.proID = proID;
        this.ordertime = ordertime;
    }

    public String orderID(){
        return orderID;
    }

    public String userID() {
        return userID;
    }

    public String proID(){
        return proID;
    }

    public String ordertime() {
        return ordertime;
    }
    public Order(){
        this.orderId = "o_12345";
        this.userId = "default_user";
        this.proId = "default_product";
        this.orderTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy_HH:mm:ss"));
    }

    @Override
    public String toString() {
        return "orderID: " + orderID + "\nuserID: " + userID + "\nproID: " + proID
                + "\nordertime: " + ordertime;
    }
}
