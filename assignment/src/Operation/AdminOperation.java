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

public class AdminOperation {
    private static AdminOperation instance;
    private JSONParser parser;
    
    // Default admin credentials
    private final String defaultAdminUserName = "admin";
    private final String defaultAdminPassword = "admin123";

   
    private AdminOperation() {
        parser = new JSONParser();
    }

    public static AdminOperation getInstance() {
        if (instance == null) { instance = new AdminOperation(); }
        return instance;    
    }

    public void registerAdmin() {
        File file = new File("assignment/data/users.txt");
        boolean adminExists = false;
        
        // Check if the admin account already exists.
        if (file.exists()) {
            try (Scanner scanner = new Scanner(file)) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine().trim();
                    if (line.isEmpty()) {
                        continue;
                    }
                    try {
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
        
        // If no admin exists, register a new default admin.
        if (!adminExists) {
            UserOperation userOp = UserOperation.getInstance();
            String userId = userOp.generateUniqueUserId();
            String registerTime = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("dd-MM-yyyy_HH:mm:ss"));
            String encryptedPwd = userOp.encryptPassword(defaultAdminPassword);

            
            HashMap<String,Object> adminDetails = new HashMap<String,Object>();
            adminDetails.put("user_id", userId);
            adminDetails.put("user_name", defaultAdminUserName);
            adminDetails.put("user_password", encryptedPwd);
            adminDetails.put("user_register_time", registerTime);
            adminDetails.put("user_role", "admin");
            JSONObject adminDetailsJSON = new JSONObject(adminDetails);

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