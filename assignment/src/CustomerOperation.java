import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class CustomerOperation {
    private static CustomerOperation instance;
    private static final int PAGE_SIZE = 10;
    private static final String FILE_PATH = "assignment/src/data/users.json";
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
            JSONParser parser = new JSONParser();

            while ((line = reader.readLine()) != null) {
                JSONObject user = (JSONObject) parser.parse(line);
                if (user.get("user_name").toString().equals(userName)) {
                    return true; 
                }
            }
        } catch (IOException | ParseException e) {
            System.err.println("Failed to check username existence: " + e.getMessage());
            e.printStackTrace();
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

        JSONObject newUserJSON = new JSONObject();
        HashMap<String,Object> newUser = new HashMap<String,Object>();
        newUser.put("user_id", generateUniqueUserId());
        newUser.put("user_name", userName);
        newUser.put("user_password", userPassword);
        newUser.put("user_register_time", LocalDateTime.now().toString());
        newUser.put("user_role", "customer");
        newUser.put("user_email", userEmail);
        newUser.put("user_mobile", userMobile);

        return writeUserToFile(newUserJSON);
    }

    public boolean updateProfile(String attributeName, String value, Customer customerObject) {
        List<String> updatedUsers = new ArrayList<>();
        boolean updated = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            JSONParser parser = new JSONParser();

            while ((line = reader.readLine()) != null) {
                JSONObject user = (JSONObject) parser.parse(line);
                if (user.get("user_id").toString().equals(customerObject.getUserId())) {
                    HashMap<String,Object> userDetails = new HashMap<String,Object>();
                    userDetails.put(attributeName, value);
                    updatedUsers.add(user.toJSONString());
                    updated = true;
                } else {
                    updatedUsers.add(line);
                }
            }
        } catch (IOException | ParseException e) {
            System.err.println("Failed to update profile: " + e.getMessage());
            e.printStackTrace();
            return false;
        }

        return updated && writeUsersToFile(updatedUsers);
    }

    public boolean deleteCustomer(String customerId) {
        List<String> updatedUsers = new ArrayList<>();
        boolean deleted = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            JSONParser parser = new JSONParser();

            while ((line = reader.readLine()) != null) {
                JSONObject user = (JSONObject) parser.parse(line);
                if (!user.get("user_id").toString().equals(customerId)) {
                    updatedUsers.add(line);
                } else {
                    deleted = true;
                }
            }
        } catch (IOException | ParseException e) {
            System.err.println("Failed to delete customer: " + e.getMessage());
            e.printStackTrace();
            return false;
        }

        return deleted && writeUsersToFile(updatedUsers);
    }

    public CustomerListResult getCustomerList(int pageNumber) {
        List<Customer> customers = new ArrayList<>();
        int totalCustomers = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            JSONParser parser = new JSONParser();

            while ((line = reader.readLine()) != null) {
                totalCustomers++;
                if (totalCustomers > (pageNumber - 1) * PAGE_SIZE && customers.size() < PAGE_SIZE) {
                    try {
                        JSONObject user = (JSONObject) parser.parse(line);
                        customers.add(new Customer(user));
                    } catch (ParseException e) {
                        System.err.println("Failed to parse JSON for a customer: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to read customers: " + e.getMessage());
        }

        return new CustomerListResult(customers, pageNumber, (int) Math.ceil((double) totalCustomers / PAGE_SIZE));
    }

    public void deleteAllCustomers() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            writer.write("");
        } catch (IOException e) {
            System.err.println("Failed to delete all customers: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean writeUserToFile(JSONObject user) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(user.toJSONString());
            writer.newLine();
            return true;
        } catch (IOException e) {
            System.err.println("Failed to write user to file: " + e.getMessage());
            e.printStackTrace();
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
            System.err.println("Failed to update file: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public Customer getCustomerById(String customerId) {
    try (BufferedReader reader = new BufferedReader(new FileReader("./src/users.json"))) {
        String line;
        JSONParser parser = new JSONParser();

        while ((line = reader.readLine()) != null) {
            JSONObject customerJson = (JSONObject) parser.parse(line);
            if (customerJson.get("customer_id").toString().equals(customerId)) {
                return new Customer(customerJson); 
            }
        }
    } catch (IOException | ParseException e) {
        System.err.println("Failed to retrieve customer: " + e.getMessage());
    }
        return null; 
    }

}