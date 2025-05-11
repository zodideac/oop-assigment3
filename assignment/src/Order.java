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
        this.oderID = orderID;
        this.userID = userID;
        this.proID = proID;
        this.ordertime = ordertime;
    }

    @Override
    public String toString() {
        return "orderID: " + orderIDID + "\nuserID: " + userID + "\nproID: " + proID
                + "\nordertime: " + ordertime;
    }
}