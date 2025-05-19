package Operation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * The {@code AdminOperation} class encapsulates operations related to the administrator
 * account. It provides functionality to register the default admin account if one does not
 * already exist in the system. The class is implemented as a singleton to ensure that only one
 * instance is used throughout the application.
 */
public class AdminOperation {

    /** Singleton instance of the AdminOperation class. */
    private static AdminOperation instance;

    /** JSON parser used for processing user data from the users file. */
    private JSONParser parser;

    // Default admin credentials.
    private final String defaultAdminUserName = "admin";
    private final String defaultAdminPassword = "admin123";

    /**
     * Private constructor to enforce the singleton pattern.
     * Initializes the JSON parser.
     */
    private AdminOperation() {
        parser = new JSONParser();
    }

    /**
     * Retrieves the singleton instance of {@code AdminOperation}. If an instance does not exist yet,
     * a new one is created.
     *
     * @return the singleton instance of AdminOperation.
     */
    public static AdminOperation getInstance() {
        if (instance == null) {
            instance = new AdminOperation();
        }
        return instance;
    }

    /**
     * Registers the default admin account if no admin account exists in the users file.
     * <p>
     * The method checks "assignment/data/users.txt" for any user record with a role of "admin".
     * If none is found, it registers a new admin using the default credentials. The new admin's
     * password is encrypted before saving, and the registration timestamp is recorded using the
     * "dd-MM-yyyy_HH:mm:ss" format.
     * </p>
     */
    public void registerAdmin() {
        File file = new File("assignment/data/users.txt");
        boolean adminExists = false;
        
        // Check if the admin account already exists in the users file.
        if (file.exists()) {
            try (Scanner scanner = new Scanner(file)) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine().trim();
                    if (line.isEmpty()) {
                        continue;
                    }
                    try {
                        // Parse each JSON record and check the user role.
                        JSONObject json = (JSONObject) parser.parse(line);
                        String role = (String) json.get("user_role");
                        if ("admin".equalsIgnoreCase(role)) {
                            adminExists = true;
                            break;
                        }
                    } catch (ParseException pe) {
                        System.err.println("Error parsing JSON while checking admin existence: " + pe.getMessage());
                    }
                }
            } catch (IOException e) {
                System.err.println("Error reading user file: " + e.getMessage());
            }
        }
        
        // If admin does not exist, create a new admin account.
        if (!adminExists) {
            // Get an instance of UserOperation for utility functions.
            UserOperation userOp = UserOperation.getInstance();
            // Generate a unique user ID for the admin.
            String userId = userOp.generateUniqueUserId();
            // Get the current time as the registration time.
            String registerTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy_HH:mm:ss"));
            // Encrypt the default admin password.
            String encryptedPwd = userOp.encryptPassword(defaultAdminPassword);

            // Create a map to hold admin details.
            HashMap<String, Object> adminDetails = new HashMap<String, Object>();
            adminDetails.put("user_id", userId);
            adminDetails.put("user_name", defaultAdminUserName);
            adminDetails.put("user_password", encryptedPwd);
            adminDetails.put("user_register_time", registerTime);
            adminDetails.put("user_role", "admin");

            // Convert the admin details map to a JSON object.
            JSONObject adminDetailsJSON = new JSONObject(adminDetails);

            // Append the new admin record to the users file.
            try (FileWriter writer = new FileWriter("assignment/data/users.txt", true)) {
                writer.write(adminDetailsJSON.toJSONString() + System.lineSeparator());
                System.out.println("Default admin registered successfully. Username: " + defaultAdminUserName);
            } catch (IOException e) {
                System.err.println("Error writing admin info to user file: " + e.getMessage());
            }
        } else {
            System.out.println("Admin account already exists.");
        }
    }
}