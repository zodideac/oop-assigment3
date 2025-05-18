import java.io.*;
import java.util.HashMap;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class AdminOperation {
    private static AdminOperation instance;
    private static final String FILE_PATH = "assignment/src/data/users.json";
    private AdminOperation() {}

    public static AdminOperation getInstance() {
        if (instance == null) { instance = new AdminOperation(); }
        return instance;
    }

    public void registerAdmin() {
        if (adminExists()) {
            System.out.println("Admin already registered. Skipping registration.");
            return;
        }

        Admin admin = new Admin();
        HashMap<String,Object> adminJson = new HashMap<String,Object>();
        adminJson.put("user_id", admin.userID());
        adminJson.put("user_name", admin.userName());
        adminJson.put("user_password", admin.userPassword());
        adminJson.put("user_register_time", admin.userRegisterTime());
        adminJson.put("user_role", admin.userRole());
        JSONObject adminJsonJSON = new JSONObject(adminJson);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) { 
            writer.write(adminJsonJSON.toJSONString()); 
            writer.newLine();
            System.out.println("Admin account registered successfully.");
        } catch (IOException e) {
            System.err.println("Failed to register admin: " + e.getMessage());
            e.printStackTrace(); 
        }
    }

    private boolean adminExists() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            JSONParser parser = new JSONParser();

            while ((line = reader.readLine()) != null) {
                JSONObject adminJson = (JSONObject) parser.parse(line);
                if ("admin".equals(adminJson.get("user_role"))) {
                    return true; 
                }
            }
        } catch (IOException | ParseException e) {
            System.err.println("Failed to check admin existence: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}