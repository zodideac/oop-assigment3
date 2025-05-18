import java.util.*;
import java.io.*;

public class UserOperation {
  private static UserOperation instance;

  private UserOperation() {}

  public static UserOperation getInstance() {
    if (instance == null) { instance = new UserOperation(); }
    return instance;
  }

  public String generateUniqueUserId() {
    Random random = new Random();
    long uniqueNumber = 1000000000L + (long)(random.nextDouble() * 9000000000L);
    return "u_" + uniqueNumber;
  }

  public String encryptPassword(String userPassword) {
    if (userPassword == null || userPassword.isEmpty()) { return null; }

    String charPool = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    Random random = new Random();
    StringBuilder randomString = new StringBuilder();

    for (int i = 0; i < userPassword.length() * 2; i++) {
      randomString.append(charPool.charAt(random.nextInt(charPool.length())));
    }

    StringBuilder encrypted = new StringBuilder("^^");

    for (int i = 0; i < userPassword.length(); i++) {
      encrypted.append(randomString.substring(i * 2, i * 2 + 2));
      encrypted.append(userPassword.charAt(i));
    }

    encrypted.append("$$");
    return encrypted.toString();
  }

  public String decryptPassword(String encryptedPassword) {
    if (encryptedPassword == null || encryptedPassword.length() < 4) { return null; }

    encryptedPassword = encryptedPassword.substring(2, encryptedPassword.length() - 2);
    StringBuilder originalPassword = new StringBuilder();

    for (int i = 0; i < encryptedPassword.length(); i += 3) {
      originalPassword.append(encryptedPassword.charAt(i + 2));
    }
    return originalPassword.toString();
  }

  public boolean checkUsernameExist(String userName) {
        File file = new File("assignment/src/data/users.json");
        if (!file.exists()) {
            return false; 
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("\"user_name\":\"" + userName + "\"")) {
                    return true; 
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to read users file: " + e.getMessage());
        }
        return false;
  }
  
  public boolean validateUsername(String userName) {
    if (userName == null || userName.length() < 5) { return false; }

    for (char c : userName.toCharArray()) {
      if (!Character.isLetter(c) && c != '_') { return false; }
    }
    return true;
  }

  public boolean validatePassword(String userPassword) {
    if (userPassword == null || userPassword.length() < 5) { return false; }

    boolean hasLetter = false, hasDigit = false;

    for (char c : userPassword.toCharArray()) {
      if (Character.isLetter(c)) { hasLetter = true; }
      if (Character.isDigit(c)) { hasDigit = true; }
    }
    return hasLetter && hasDigit;
  }

  public User login(String userName, String userPassword) {
    return null; 
  }
}