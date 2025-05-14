import java.io.*;
import org.json.JSONObject;


public class AdminOperation {
    private static AdminOperation instance;
    private static final String FILE_PATH = "./data/admin.txt";

    private AdminOperation() {}

    public static AdminOperation getInstance() {
        if (instance == null) {
            instance = new AdminOperation();
        }
        return instance;
    }

    public void registerAdmin() {
        if (adminExists()) {
            System.out.println("Admin already registered. Skipping registration.");
            return;
        }

        Admin admin = new Admin();
        JSONObject adminJson = new JSONObject();
        adminJson.put("user_id", admin.userID());
        adminJson.put("user_name", admin.userName());
        adminJson.put("user_password", admin.userPassword());
        adminJson.put("user_register_time", admin.userRegisterTime());
        adminJson.put("user_role", admin.userRole());

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            writer.write(adminJson.toString());
            writer.newLine();
            System.out.println("Admin account registered successfully.");
        } catch (IOException e) {
            System.out.println("Error registering admin: " + e.getMessage());
        }
    }

    private boolean adminExists() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            return reader.readLine() != null;
        } catch (IOException e) {
            System.out.println("Error checking admin existence: " + e.getMessage());
            return false;
        }
    }
}