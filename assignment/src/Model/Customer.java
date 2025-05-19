package Model;

/**
 * The {@code Customer} class represents a user with a "customer" role in the system.
 * It extends the {@code User} class by adding additional details specific to customers,
 * such as an email address and a mobile phone number.
 *
 * <p>This class provides two constructors:
 * <ul>
 *   <li>A fully parameterized constructor for creating a customer with all details.</li>
 *   <li>A default constructor that initializes the customer with placeholder values.</li>
 * </ul>
 * </p>
 */
public class Customer extends User {
    // The customer's email address.
    private String userEmail;
    // The customer's mobile phone number.
    private String userMobile;

    /**
     * Constructs a new {@code Customer} with the specified details.
     * Note that regardless of the passed userRole, the role will be set to "customer".
     *
     * @param userId           the unique identifier for the customer
     * @param userName         the customer's name
     * @param userPassword     the customer's password
     * @param userRegisterTime the registration time, formatted as "dd-MM-yyyy_HH:mm:ss"
     * @param userRole         the user role (this parameter is ignored and replaced by "customer")
     * @param userEmail        the customer's email address
     * @param userMobile       the customer's mobile phone number
     */
    public Customer(String userId, String userName, String userPassword, 
                    String userRegisterTime, String userRole,
                    String userEmail, String userMobile) {
        super(userId, userName, userPassword, userRegisterTime, "customer");
        this.userEmail = userEmail;
        this.userMobile = userMobile;
    }

    /**
     * Retrieves the customer's email address.
     *
     * @return the customer's email address
     */
    public String getUserEmail() {
        return userEmail;
    }

    /**
     * Retrieves the customer's mobile number.
     *
     * @return the customer's mobile number
     */
    public String getUserMobile() {
        return userMobile;
    }

    /**
     * Sets the customer's email address.
     *
     * @param userEmail the new email address to set
     */
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    /**
     * Sets the customer's mobile number.
     *
     * @param userMobile the new mobile number to set
     */
    public void setUserMobile(String userMobile) {
        this.userMobile = userMobile;
    }

    /**
     * Default constructor that initializes a customer with placeholder values.
     * <p>
     * The default values are:
     * <ul>
     *   <li>Email: "default@gmail.com"</li>
     *   <li>Mobile: "0400000000"</li>
     * </ul>
     * </p>
     */
    public Customer() {
        super();
        this.userEmail = "default@gmail.com";
        this.userMobile = "0400000000";
    }

    /**
     * Returns a JSON-formatted string representation of the customer.
     * This string includes the details inherited from {@code User} as well as the customer's
     * email and mobile number.
     *
     * @return a JSON string representing the customer's data
     */
    @Override
    public String toString() {
        return String.format("{\"user_id\":\"%s\",\"user_name\":\"%s\",\"user_password\":\"%s\"," +
                             "\"user_register_time\":\"%s\",\"user_role\":\"%s\"," +
                             "\"user_email\":\"%s\",\"user_mobile\":\"%s\"}",
                             userId, userName, userPassword, userRegisterTime, userRole, userEmail, userMobile);
    }
}