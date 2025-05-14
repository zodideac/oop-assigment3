import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import org.json.*;

public class CustomerOperation {
    private static CustomerOperation instance;
    private static final String FILE_PATH = "./data/users.txt";

    private CustomerOperation() {}

    public static CustomerOperation getInstance() {
        if (instance == null) {
            instance = new CustomerOperation();
        }
        return instance;
    }

    public boolean validateEmail(String userEmail) {
        if (userEmail == null || !userEmail.contains("@")) return false;
        String[] parts = userEmail.split("@");
        return parts.length == 2 && parts[1].contains(".");
    }

    public boolean validateMobile(String userMobile) {
        return userMobile != null && userMobile.matches("(04|03)\\d{8}");
    }

    public boolean registerCustomer(String userName, String userPassword, String userEmail, String userMobile) {
        if (!validateUsername(userName) || !validatePassword(userPassword) || !validateEmail(userEmail) || !validateMobile(userMobile)) {
            return false;
        }

        if (checkUsernameExist(userName)) {
            return false;
        }

        String userID = generateUniqueUserId();
        String encryptedPassword = encryptPassword(userPassword);
        String registerTime = LocalDateTime.now().toString();

        JSONObject newUser = new JSONObject();
        newUser.put("user_id", userID);
        newUser.put("user_name", userName);
        newUser.put("user_password", encryptedPassword);
        newUser.put("user_register_time", registerTime);
        newUser.put("user_role", "customer");
        newUser.put("user_email", userEmail);
        newUser.put("user_mobile", userMobile);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(newUser.toString());
            writer.newLine();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean updateProfile(String attributeName, String value, String customerId) {
        List<String> updatedUsers = new ArrayList<>();
        boolean updated = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                JSONObject user = new JSONObject(line);
                if (user.getString("user_id").equals(customerId)) {
                    user.put(attributeName, value);
                    updatedUsers.add(user.toString());
                    updated = true;
                } else {
                    updatedUsers.add(line);
                }
            }
        } catch (IOException e) {
            return false;
        }

        if (updated) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
                for (String user : updatedUsers) {
                    writer.write(user);
                    writer.newLine();
                }
                return true;
            } catch (IOException e) {
                return false;
            }
        }
        return false;
    }

    public boolean deleteCustomer(String customerId) {
        List<String> updatedUsers = new ArrayList<>();
        boolean deleted = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                JSONObject user = new JSONObject(line);
                if (!user.getString("user_id").equals(customerId)) {
                    updatedUsers.add(line);
                } else {
                    deleted = true;
                }
            }
        } catch (IOException e) {
            return false;
        }

        if (deleted) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
                for (String user : updatedUsers) {
                    writer.write(user);
                    writer.newLine();
                }
                return true;
            } catch (IOException e) {
                return false;
            }
        }
        return false;
    }

    public CustomerListResult getCustomerList(int pageNumber) {
        List<Customer> customers = new ArrayList<>();
        int totalCustomers = 0;
        int pageSize = 10;

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                totalCustomers++;
                if (totalCustomers > (pageNumber - 1) * pageSize && customers.size() < pageSize) {
                    JSONObject user = new JSONObject(line);
                    customers.add(new Customer(
                        user.getString("user_id"),
                        user.getString("user_name"),
                        user.getString("user_password"),
                        user.getString("user_register_time"),
                        user.getString("user_role"),
                        user.optString("user_email", ""),
                        user.optString("user_mobile", "")
                    ));
                }
            }
        } catch (IOException e) {
        }

        return new CustomerListResult(customers, pageNumber, (int) Math.ceil((double) totalCustomers / pageSize));
    }

    public void deleteAllCustomers() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            writer.write("");
        } catch (IOException e) {
        }
    }
}