package Model;

/**
 * The {@code User} abstract class defines the basic attributes and behaviors for a user in the system.
 * It contains properties such as userId, userName, userPassword, userRegisterTime, and userRole,
 * along with their respective getters and setters.
 * <p>
 * This class provides two constructors: one that allows you to set all properties explicitly and a default
 * constructor that initializes the user with placeholder values. You can extend this class by creating concrete
 * subclasses (for example, for customers or administrators).
 * </p>
 */
public abstract class User {
    // Unique user identifier (expected format: "u_xxxxxxxxxx")
    protected String userId;
    // The username of the user
    protected String userName;
    // The user's password (could be plain text or encrypted)
    protected String userPassword; 
    // The registration timestamp in the format "dd-MM-yyyy_HH:mm:ss"
    protected String userRegisterTime;
    // The role of the user (e.g., "customer" or "admin")
    protected String userRole; 

    /**
     * Constructs a new {@code User} with the specified details.
     *
     * @param userId the unique identifier for the user
     * @param userName the username
     * @param userPassword the user's password
     * @param userRegisterTime the registration time as a formatted string
     * @param userRole the user's role (for example, "customer" or "admin")
     */
    public User(String userId, String userName, String userPassword, 
                String userRegisterTime, String userRole) {
        this.userId = userId;
        this.userName = userName;
        this.userPassword = userPassword;
        this.userRegisterTime = userRegisterTime;
        this.userRole = userRole;
    }

    /**
     * Returns the user's unique identifier.
     *
     * @return the user id
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Returns the user's name.
     *
     * @return the user name
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Returns the user's password.
     *
     * @return the user password
     */
    public String getUserPassword() {
        return userPassword;
    }

    /**
     * Returns the registration time of the user.
     *
     * @return the registration timestamp
     */
    public String getUserRegisterTime() {
        return userRegisterTime;
    }

    /**
     * Returns the role of the user.
     *
     * @return the user role
     */
    public String getUserRole() {
        return userRole;
    }

    /**
     * Sets a new unique identifier for the user.
     *
     * @param userId the new user id
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Updates the username.
     *
     * @param userName the new username
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Updates the user's password.
     *
     * @param userPassword the new password
     */
    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    /**
     * Updates the registration timestamp.
     *
     * @param userRegisterTime the new registration time, formatted as "dd-MM-yyyy_HH:mm:ss"
     */
    public void setUserRegisterTime(String userRegisterTime) {
        this.userRegisterTime = userRegisterTime;
    }

    /**
     * Sets a new user role for this user.
     *
     * @param userRole the new role (e.g., "customer" or "admin")
     */
    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    /**
     * Default constructor that initializes the user with default placeholder values.
     * <p>
     * For testing or fallback purposes, the values are initialized as follows:
     * <ul>
     *   <li>userId to "u_0000000000"</li>
     *   <li>userName to "defaultUser"</li>
     *   <li>userPassword to "defaultPassword"</li>
     *   <li>userRegisterTime to "01-01-1970_00:00:00"</li>
     *   <li>userRole to "customer"</li>
     * </ul>
     * </p>
     */
    public User() {
        this.userId = "u_0000000000";
        this.userName = "defaultUser";
        this.userPassword = "defaultPassword";
        this.userRegisterTime = "01-01-1970_00:00:00";
        this.userRole = "customer";
    }

    /**
     * Returns a JSON-formatted string representation of this user.
     * The output includes the user id, name, password, registration time, and role.
     *
     * @return a JSON string representing the user
     */
    @Override
    public String toString() {
        return String.format("{\"user_id\":\"%s\",\"user_name\":\"%s\",\"user_password\":\"%s\","
                + "\"user_register_time\":\"%s\",\"user_role\":\"%s\"}",
                userId, userName, userPassword, userRegisterTime, userRole);
    }
}