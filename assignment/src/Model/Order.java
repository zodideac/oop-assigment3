package Model;

/**
 * The {@code Order} class represents an order placed by a user for a given product.
 * It contains basic details including a unique order identifier, the identifier of the user
 * who placed the order, the identifier of the product being ordered, and the timestamp of the order.
 * <p>
 * The class provides a parameterized constructor for instantiating an order with specific
 * details as well as a default constructor that initializes an order with placeholder values,
 * which may be useful for testing or fallback scenarios.
 * </p>
 */
public class Order {
    // Unique identifier for the order (e.g., "o_00000")
    private String orderId;
    // Identifier for the user who placed the order (e.g., "u_0000000000")
    private String userId;
    // Identifier for the product associated with this order.
    private String proId;
    // Timestamp representing when the order was placed (format: "dd-MM-yyyy_HH:mm:ss").
    private String orderTime;

    /**
     * Constructs a new {@code Order} with the specified order details.
     *
     * @param orderId   the unique identifier for the order
     * @param userId    the identifier of the user who placed the order
     * @param proId     the identifier of the product in the order
     * @param orderTime the time at which the order was placed, formatted as "dd-MM-yyyy_HH:mm:ss"
     */
    public Order(String orderId, String userId, String proId, String orderTime) {
        this.orderId = orderId;
        this.userId = userId;
        this.proId = proId;
        this.orderTime = orderTime;
    }

    /**
     * Default constructor that initializes the order with placeholder values.
     * <p>
     * The default values are:
     * <ul>
     *   <li>orderId: "o_00000"</li>
     *   <li>userId: "u_0000000000"</li>
     *   <li>proId: "defaultPro"</li>
     *   <li>orderTime: "01-01-1970_00:00:00"</li>
     * </ul>
     * </p>
     */
    public Order() {
        this.orderId = "o_00000";
        this.userId = "u_0000000000";
        this.proId = "defaultPro";
        this.orderTime = "01-01-1970_00:00:00";
    }

    /**
     * Returns the order's unique identifier.
     *
     * @return the order id
     */
    public String getOrderId() {
        return orderId;
    }

    /**
     * Sets the unique identifier for the order.
     *
     * @param orderId the order id to set
     */
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    /**
     * Returns the identifier of the user who placed the order.
     *
     * @return the user id
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the user id for this order.
     *
     * @param userId the user id to set
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Returns the product identifier associated with this order.
     *
     * @return the product id
     */
    public String getProId() {
        return proId;
    }

    /**
     * Sets the product id for this order.
     *
     * @param proId the product id to set
     */
    public void setProId(String proId) {
        this.proId = proId;
    }

    /**
     * Returns the time when the order was placed.
     *
     * @return the order time as a formatted string
     */
    public String getOrderTime() {
        return orderTime;
    }

    /**
     * Sets the timestamp for when the order was placed.
     *
     * @param orderTime the order time to set, formatted as "dd-MM-yyyy_HH:mm:ss"
     */
    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    /**
     * Returns a JSON-formatted string representation of this order.
     * The output includes the order id, user id, product id, and order time.
     *
     * @return a JSON string representing the order
     */
    @Override
    public String toString() {
        return String.format("{\"order_id\":\"%s\",\"user_id\":\"%s\",\"pro_id\":\"%s\"," +
                             "\"order_time\":\"%s\"}",
                             orderId, userId, proId, orderTime);
    }
}