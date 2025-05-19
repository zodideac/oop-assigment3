package Model;

/**
 * The {@code Admin} class represents an administrator user in the system.
 * It is a subclass of {@code User} and always sets the user role to "admin".
 * <p>
 * This class provides two constructors:
 * <ul>
 *   <li>A parameterized constructor to create an admin with specific details.</li>
 *   <li>A default constructor that initializes the admin with default placeholder values.</li>
 * </ul>
 * </p>
 */
public class Admin extends User {

    /**
     * Constructs a new {@code Admin} with the specified details.
     * Even though a user role is passed as a parameter, the role is set to "admin".
     *
     * @param userId           the unique identifier for the admin
     * @param userName         the admin's name
     * @param userPassword     the admin's password
     * @param userRegisterTime the registration time formatted as "dd-MM-yyyy_HH:mm:ss"
     * @param userRole         the user role (this parameter is ignored and "admin" is used instead)
     */
    public Admin(String userId, String userName, String userPassword, 
                 String userRegisterTime, String userRole) {
        // Call the superclass constructor and force the user role to "admin"
        super(userId, userName, userPassword, userRegisterTime, "admin");
    }

    /**
     * Default constructor that initializes an admin with default placeholder values.
     * <p>
     * This constructor first calls the default constructor of {@code User} and then explicitly
     * sets the user role to "admin".
     * </p>
     */
    public Admin() {
        super();
        // Ensure that the admin role is set correctly even if the superclass default differs.
        this.userRole = "admin";
    }

    /**
     * Returns a JSON-formatted string representation of the admin.
     * The string includes the admin's user ID, name, password, registration time, and role.
     *
     * @return a JSON string representing this admin
     */
    @Override
    public String toString() {
        return String.format("{\"user_id\":\"%s\",\"user_name\":\"%s\",\"user_password\":\"%s\"," +
                             "\"user_register_time\":\"%s\",\"user_role\":\"%s\"}",
                             userId, userName, userPassword, userRegisterTime, userRole);
    }
}