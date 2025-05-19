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

/**
 * The {@code UserOperation} class provides utility methods for managing users,
 * such as generating unique user IDs, encrypting/decrypting passwords, validating
 * credentials, and handling login operations. This class is implemented as a singleton,
 * ensuring that only one instance exists throughout the application.
 */
public class UserOperation {

    /** Singleton instance of UserOperation. */
    private static UserOperation instance;

    /** JSON parser for processing user data stored in JSON format. */
    private JSONParser parser;

    /**
     * Private constructor to initialize the JSON parser.
     */
    private UserOperation() {
        parser = new JSONParser();
    }

    /**
     * Retrieves the single instance of {@code UserOperation}. If it does not exist yet,
     * the instance is created.
     *
     * @return the singleton instance of UserOperation.
     */
    public static UserOperation getInstance() {
        if (instance == null) { 
            instance = new UserOperation();
        }
        return instance;
    }

    /**
     * Generates a unique user identifier in the format "u_xxxxxxxxxx".
     *
     * @return a unique user ID string.
     */
    public String generateUniqueUserId() {
        return "u_" + String.format("%010d", new Random().nextInt(1000000000));
    }

    /**
     * Generates a random alphanumeric string of the specified length.
     *
     * @param length the length of the desired random string.
     * @return a randomly generated string.
     */
    public String generateRandomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    /**
     * Encrypts the given plain text password using a simple algorithm.
     * <p>
     * The encryption process is as follows:
     * <ul>
     *   <li>Generate a random string of length equal to two times the password length.</li>
     *   <li>Begin the encrypted password with the starting marker "^^".</li>
     *   <li>For each character in the password, append two corresponding random characters
     *       and then the original character.</li>
     *   <li>Finish the encrypted password with the ending marker "$$".</li>
     * </ul>
     * </p>
     *
     * @param userPassword the plain text password to encrypt.
     * @return the encrypted password, or null if the provided password is null or empty.
     */
    String encryptPassword(String userPassword) {
        if (userPassword == null || userPassword.isEmpty()) return null;
        // Initialize the encrypted password with the starting marker.
        StringBuilder encrypted = new StringBuilder("^^");
        // Generate the random characters needed for encryption.
        String randomChars = UserOperation.getInstance().generateRandomString(userPassword.length() * 2);
        // For every character of the input password, append two random characters and then the character.
        for (int i = 0; i < userPassword.length(); i++) {
            encrypted.append(randomChars.charAt(i * 2))
                     .append(randomChars.charAt(i * 2 + 1))
                     .append(userPassword.charAt(i));
        }
        // Append the ending marker.
        encrypted.append("$$");
        return encrypted.toString();
    }

    /**
     * Decrypts an encrypted password produced by {@link #encryptPassword(String)}.
     * <p>
     * The method assumes the following encryption format:
     * <ul>
     *   <li>The encrypted password starts with "^^" and ends with "$$".</li>
     *   <li>The remaining content is grouped in blocks of 3 characters (two random + original),
     *       so the original password is composed by every third character starting at index 2.</li>
     * </ul>
     * </p>
     *
     * @param encryptedPassword the encrypted password string.
     * @return the decrypted plain text password, or null if the input is null/too short.
     */
    public String decryptPassword(String encryptedPassword) {
        if (encryptedPassword == null || encryptedPassword.length() < 5) return null;
        // Remove the starting "^^" and ending "$$" markers.
        encryptedPassword = encryptedPassword.substring(2, encryptedPassword.length() - 2);
        StringBuilder decrypted = new StringBuilder();
        // For every 3-character block, extract the 3rd character.
        for (int i = 2; i < encryptedPassword.length(); i += 3) {
            decrypted.append(encryptedPassword.charAt(i));
        }
        return decrypted.toString();
    }

    /**
     * Validates the username based on the following criteria:
     * <ul>
     *   <li>The username must not be null.</li>
     *   <li>The username must be at least 5 characters long.</li>
     *   <li>The username may only contain uppercase letters, lowercase letters, and underscores.</li>
     * </ul>
     *
     * @param userName the username to validate.
     * @return true if the username is valid; false otherwise.
     */
    public boolean validateUsername(String userName) {
        return userName != null && userName.matches("[A-Za-z_]{5,}");
    }

    /**
     * Validates the password based on the following criteria:
     * <ul>
     *   <li>The password must not be null.</li>
     *   <li>The password must be at least 5 characters long.</li>
     *   <li>The password must contain at least one letter and one digit.</li>
     * </ul>
     *
     * @param userPassword the password to validate.
     * @return true if the password is valid; false otherwise.
     */
    public boolean validatePassword(String userPassword) {
        return userPassword != null && userPassword.matches("(?=.*[A-Za-z])(?=.*\\d).{5,}");
    }

    /**
     * Checks whether a given username already exists in the users file.
     * <p>
     * The method reads the file "assignment/data/users.txt", which is expected to contain one JSON-formatted
     * record per line, and searches for a matching "user_name" field.
     * </p>
     *
     * @param userName the username to search for.
     * @return true if the username is found; false otherwise.
     */
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
                    // Parse the JSON record.
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

    /**
     * Attempts to log in a user by validating the provided username and password against
     * records stored in "assignment/data/users.txt".
     * <p>
     * The method parses each JSON-formatted record, decrypts the stored password,
     * and checks if the decrypted password matches the provided password.
     * If the decryption fails for an admin user (possibly stored using an older encryption format),
     * it falls back to comparing against a default admin password ("admin123").
     * </p>
     *
     * @param username the username entered by the user.
     * @param password the plain text password entered by the user.
     * @return a {@code User} object (either an {@code Admin} or {@code Customer}) if login is successful; null otherwise.
     */
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
                    // Parse the current line into a JSON object.
                    JSONObject json = (JSONObject) parser.parse(line);
                    String storedUsername = (String) json.get("user_name");
                    String storedPassword = (String) json.get("user_password");
                    
                    boolean valid = false;
                    try {
                        // Attempt to decrypt the stored password.
                        String decrypted = UserOperation.getInstance().decryptPassword(storedPassword);
                        if (storedUsername.equals(username) && decrypted.equals(password)) {
                            valid = true;
                        }
                    } catch (IllegalArgumentException decryptionError) {
                        // If decryption fails, check if this might be the admin account stored in legacy format.
                        if ("admin".equalsIgnoreCase(storedUsername) &&
                            storedUsername.equalsIgnoreCase(username)) {
                            // Fallback: compare with the known default admin password.
                            if ("admin123".equals(password)) {
                                valid = true;
                                System.out.println("Fallback: Using default admin password due to legacy encryption format.");
                            }
                        } else {
                            System.err.println("Error decrypting password for user \"" + storedUsername +
                                               "\": " + decryptionError.getMessage());
                        }
                    }
                    
                    if (valid) {
                        // Create a user object based on the user role from the JSON record.
                        String userRole = (String) json.get("user_role");
                        User user;
                        if ("admin".equalsIgnoreCase(userRole)) {
                            user = new Admin();
                        } else {
                            user = new Customer();
                        }
                        // Set common user data from the JSON record.
                        user.setUserId((String) json.get("user_id"));
                        user.setUserName(storedUsername);
                        user.setUserRole(userRole);
                        // Additional fields can be set here if needed.
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
        return null; // Return null if no valid user is found.
    }
}