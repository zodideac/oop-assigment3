import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import org.json.JSONObject;

public class CustomerOperation {
    private static CustomerOperation instance;
    private static final String FILE_PATH = "./data/users.txt";

    private CustomerOperation() {}

    public static CustomerOperation getInstance() {
        if (instance == null) { instance = new CustomerOperation(); }
        return instance;
    }

    public boolean validateEmail(String userEmail) {
        return userEmail != null && userEmail.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");
    }


    public boolean validateMobile(String userMobile) {
        return userMobile != null && userMobile.matches("(04|03)\\d{8}");
    }

    private boolean validateUsername(String userName) {
        return userName != null && userName.matches("[a-zA-Z0-9]{5,}");
    }

    private boolean validatePassword(String userPassword) {
        return userPassword != null && userPassword.length() >= 8;
    }

    private String generateUniqueUserId() {
        return "U_" + System.currentTimeMillis(); 
    }

    private boolean checkUsernameExist(String userName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                JSONObject user = new JSONObject(line);
                if (user.getString("user_name").equals(userName)) {
                    return true; // Username exists
                }
            }
        } catch (IOException e) {
            System.out.println("Failed to read file: " + e.getMessage());
        }

    return false; 
    }


    public boolean registerCustomer(String userName, String userPassword, String userEmail, String userMobile) {
        if (!validateUsername(userName) || !validatePassword(userPassword) || !validateEmail(userEmail) || !validateMobile(userMobile)) {
            return false;
        }

        if (checkUsernameExist(userName)) {
            return false;
        }

        String userID = generateUniqueUserId();
        String registerTime = LocalDateTime.now().toString();

        JSONObject newUser = new JSONObject();
        newUser.put("user_id", userID);
        newUser.put("user_name", userName);
        newUser.put("user_password", userPassword); 
        newUser.put("user_register_time", registerTime);
        newUser.put("user_role", "customer");
        newUser.put("user_email", userEmail);
        newUser.put("user_mobile", userMobile);

        return writeUserToFile(newUser);
    }

    public class CustomerListResult {
        private List<Customer> customers;
        private int currentPage;
        private int totalPages;

        public CustomerListResult(List<Customer> customers, int currentPage, int totalPages) {
            this.customers = customers;
            this.currentPage = currentPage;
            this.totalPages = totalPages;
        }

        public List<Customer> getCustomers() {
            return customers;
        }

        public int getCurrentPage() {
            return currentPage;
        }

        public int getTotalPages() {
            return totalPages;
        }
    }

    public boolean updateProfile(String attributeName, String value, Customer customerObject) {
        List<String> updatedUsers = new ArrayList<>();
        boolean updated = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                JSONObject user = new JSONObject(line);
                if (user.getString("user_id").equals(customerObject.userID())) { 
                    user.put(attributeName, value);
                    updatedUsers.add(user.toString());
                    updated = true;
                } else {
                    updatedUsers.add(line);
                }
            }
        } catch (IOException e) {
            System.out.println("Failed to read file: " + e.getMessage());
         return false;
        }

        return updated ? writeUsersToFile(updatedUsers) : false;
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
            System.out.println("Failed to read file: " + e.getMessage());
            return false;
        }

        return deleted ? writeUsersToFile(updatedUsers) : false;
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
            System.out.println("Failed to read file: " + e.getMessage());
        }

        return new CustomerListResult(customers, pageNumber, (int) Math.ceil((double) totalCustomers / pageSize));
    }

    public void deleteAllCustomers() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            writer.write(""); 
        } catch (IOException e) {
            System.out.println("Failed to delete all customers: " + e.getMessage());
        }
    }

    private boolean writeUserToFile(JSONObject user) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(user.toString());
            writer.newLine();
            return true;
        } catch (IOException e) {
            System.out.println("Failed to write user to file: " + e.getMessage());
            return false;
        }
    }

    private boolean writeUsersToFile(List<String> users) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (String user : users) {
                writer.write(user);
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            System.out.println("Failed to update file: " + e.getMessage());
            return false;
        }
    }
}