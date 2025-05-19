package Operation;
import Model.User;
import Model.Customer;
import Model.Admin;
import java.util.Random;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class UserOperation {
  private static UserOperation instance;
  private JSONParser parser;

  private UserOperation() {
    parser = new JSONParser();
  }

  public static UserOperation getInstance() {
    if (instance == null) { instance = new UserOperation(); }
      return instance;    
  }    
        
  public String generateUniqueUserId() {
    return "u_" + String.format("%010d", new Random().nextInt(1000000000));
  }

  public String encryptPassword(String userPassword) {
    if (userPassword == null) {
        throw new IllegalArgumentException("Password cannot be null.");
    }
    
    int passwordLength = userPassword.length();
    
    String allowedChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    
    StringBuilder randomString = new StringBuilder();
    // Generate a random string with length equal to 2 x (password length).
    for (int i = 0; i < 2 * passwordLength; i++) {
        int randomIndex = (int)(Math.random() * allowedChars.length());
        randomString.append(allowedChars.charAt(randomIndex));
    }
    
    // Build the encrypted password.
    StringBuilder encrypted = new StringBuilder();
    // Add starting marker.
    encrypted.append("^^");
    
    // Process each character of the input password.
    for (int i = 0; i < passwordLength; i++) {
        // Take two sequential characters from the random string.
        encrypted.append(randomString.charAt(2 * i));
        encrypted.append(randomString.charAt(2 * i + 1));
        // Append the original password character.
        encrypted.append(userPassword.charAt(i));
    }
    
    // Add ending marker.
    encrypted.append("$$");
    
    return encrypted.toString();
}

public String decryptPassword(String encryptedPassword) {
    if (encryptedPassword == null) {
        throw new IllegalArgumentException("Encrypted password cannot be null.");
    }
    
    // Verify that the encrypted password uses the expected markers.
    if (!encryptedPassword.startsWith("^^") || !encryptedPassword.endsWith("$$")) {
        throw new IllegalArgumentException("Invalid encrypted password format: missing markers.");
    }
    
    // Remove the starting and ending markers.
    String body = encryptedPassword.substring(2, encryptedPassword.length() - 2);
    
    // The length of the body must be a multiple of 3.
    if (body.length() % 3 != 0) {
        throw new IllegalArgumentException("Invalid encrypted password content: unexpected length.");
    }
    
    StringBuilder originalPassword = new StringBuilder();
    
    // In every three-character block, the third character is from the original password.
    for (int i = 0; i < body.length(); i += 3) {
        originalPassword.append(body.charAt(i + 2));
    }
    
    return originalPassword.toString();
    }


    public boolean validateUsername(String userName) {
        return userName != null && userName.matches("[A-Za-z_]{5,}");
    }

    public boolean validatePassword(String userPassword) {
        return userPassword != null && userPassword.matches("(?=.*[A-Za-z])(?=.*\\d).{5,}");
    }

    public boolean checkUsernameExist(String userName) {
        File file = new File("assignment/data/users.txt");
        if (!file.exists()) {
            return false;
        }
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) {
                    continue;
                }
                try {
                    JSONObject json = (JSONObject) parser.parse(line);
                    String storedName = (String) json.get("user_name");
                    if (userName.equals(storedName)) {
                        return true;
                    }
                } catch (ParseException pe) {
                    System.err.println("Error parsing JSON: " + pe.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error checking username existence: " + e.getMessage());
        }
        return false;
    }

    public User login(String username, String password) {
    File usersFile = new File("assignment/data/users.txt");
    if (!usersFile.exists()) {
        System.err.println("User file not found!");
        return null;
    }
    try (Scanner scanner = new Scanner(usersFile)) {
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) continue;
            try {
                // Parse the JSON object from the current line.
                JSONObject json = (JSONObject) parser.parse(line);
                String storedUsername = (String) json.get("user_name");
                String storedPassword = (String) json.get("user_password");
                
                boolean valid = false;
                try {
                    // Try to decrypt the stored password using the new algorithm.
                    String decrypted = UserOperation.getInstance().decryptPassword(storedPassword);
                    if (storedUsername.equals(username) && decrypted.equals(password)) {
                        valid = true;
                    }
                } catch (IllegalArgumentException decryptionError) {
                    // If decryption fails due to unexpected format, check if this is the admin account.
                    // (Assume that only the admin account might be stored using the old format.)
                    if ("admin".equalsIgnoreCase(storedUsername) && storedUsername.equalsIgnoreCase(username)) {
                        // For fallback, compare directly with the known admin plain-text password.
                        // For instance, if you know the default admin password is "admin123":
                        if ("admin123".equals(password)) {
                            valid = true;
                            System.out.println("Fallback: Using default admin password due to legacy encryption format.");
                        }
                    } else {
                        // Log the decryption error for other users.
                        System.err.println("Error decrypting password for user \"" + storedUsername +
                                           "\": " + decryptionError.getMessage());
                    }
                }
                
                if (valid) {
                    // Build and return a User object from the parsed JSON.
                    String userRole = (String) json.get("user_role");
                    User user;
                    if ("admin".equalsIgnoreCase(userRole)) {
                        user = new Admin();
                    } else {
                        user = new Customer();
                    }
                    user.setUserId((String) json.get("user_id"));
                    user.setUserName(storedUsername);
                    user.setUserRole(userRole);
                    // Set additional fields if necessary...
                    return user;
                }
            } catch (ParseException e) {
                System.err.println("Error parsing JSON during login: \"" + line + "\". " +
                                   "Error: " + e.getMessage());
            }
        }
    } catch (IOException e) {
        System.err.println("Error reading users file: " + e.getMessage());
    }
    return null; // No matching user found.
}
}
