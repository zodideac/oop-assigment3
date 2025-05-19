package Operation;
import Model.Customer;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CustomerOperation {
    private static CustomerOperation instance;
    private JSONParser parser;
    private CustomerOperation() {
        parser = new JSONParser();
    }

    public static CustomerOperation getInstance() {
        if (instance == null) { instance = new CustomerOperation(); }

        return instance;   
    }

    public boolean validateEmail(String userEmail) {
        return userEmail != null && userEmail.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }

    public boolean validateMobile(String userMobile) {
        return userMobile != null && userMobile.matches("^(04|03)\\d{8}$");
    }

    public boolean registerCustomer(String userName, String userPassword, String userEmail, String userMobile){
        if (checkUsernameExist(userName))
            return false;
        if (!validateEmail(userEmail) || !validateMobile(userMobile))
            return false;
        String userId = generateUniqueUserId();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH:mm:ss");
        String registerTime = LocalDateTime.now().format(formatter);
        HashMap<String,Object> customerObj = new HashMap<String,Object>();
        customerObj.put("user_id", userId);
        customerObj.put("user_name", userName);
        customerObj.put("user_password", UserOperation.getInstance().encryptPassword(userPassword));
        customerObj.put("user_register_time", registerTime);
        customerObj.put("user_role", "customer");
        customerObj.put("user_email", userEmail);
        customerObj.put("user_mobile", userMobile);
        JSONObject customerObjJSON = new JSONObject(customerObj);
        writeUserToFile(customerObjJSON);
        return true;
    }

    // Writes a JSONObject representing a user to the users file
    private void writeUserToFile(JSONObject userObj) {
        File file = new File("assignment/data/users.txt");
        try (FileWriter writer = new FileWriter(file, true)) {
            writer.write(userObj.toJSONString() + System.lineSeparator());
        } catch (IOException e) {
            System.err.println("Error writing user to file: " + e.getMessage());
        }
    }

    // Checks if a username already exists in the users file
    public boolean checkUsernameExist(String userName) {
        File file = new File("assignment/data/users.txt");
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty())
                    continue;
                try {
                    JSONObject json = (JSONObject) parser.parse(line);
                    if (userName.equals(json.get("user_name"))) {
                        return true;
                    }
                } catch (ParseException e) {
                    // Ignore parse errors and continue
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        return false;
    }

    // Generates a unique user ID for a new customer
    private String generateUniqueUserId() {
        // Example: "u_" + current timestamp in milliseconds
        return "u_" + System.currentTimeMillis();
    }

    public boolean updateProfile(String attributeName, String value, Customer customerObject) {
        if (customerObject == null) {
            System.out.println("Customer does not exist.");
            return false;
        }
        List<String> lines = new ArrayList<>();
        File file = new File("assignment/data/users.txt");
        boolean found = false;

        // Read all records
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty())
                    continue;
                try {
                    JSONObject profileDetailsJSON = (JSONObject) parser.parse(line);
                    HashMap<String,Object> profileDetails = new HashMap<String,Object>();
                    if (customerObject.getUserId().equals(profileDetails.get("user_id"))) {
                        // Update based on attribute name
                        switch (attributeName) {
                            case "user_name":
                                if (!UserOperation.getInstance().validateUsername(value)) {
                                    System.out.println("Invalid username.");
                                    return false;
                                }
                                profileDetails.put("user_name", value);
                                break;
                            case "user_password":
                                if (!UserOperation.getInstance().validatePassword(value)) {
                                    System.out.println("Invalid password.");
                                    return false;
                                }
                                profileDetails.put("user_password", UserOperation.getInstance().encryptPassword(value));
                                break;
                            case "user_email":
                                if (!validateEmail(value)) {
                                    System.out.println("Invalid email.");
                                    return false;
                                }
                                profileDetails.put("user_email", value);
                                break;
                            case "user_mobile":
                                if (!validateMobile(value)) {
                                    System.out.println("Invalid mobile number.");
                                    return false;
                                }
                                profileDetails.put("user_mobile", value);
                                break;
                            default:
                                System.out.println("Attribute not allowed for update.");
                                return false;
                        }
                        found = true;
                    }
                    lines.add(profileDetailsJSON.toJSONString());
                } catch (ParseException e) {
                    System.err.println("Error parsing JSON: " + e.getMessage());
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            return false;
        }
        if (!found) {
            System.out.println("Customer not found.");
            return false;
        }

        // Write the updated records back to the file
        try (FileWriter writer = new FileWriter("assignment/data/users.txt", false)) {
            for (String l : lines) {
                writer.write(l + System.lineSeparator());
            }
            return true;
        } catch (IOException e) {
            System.err.println("Error writing file: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteCustomer(String customerId) {
        List<String> lines = new ArrayList<>();
        File file = new File("assignment/data/users.txt");
        boolean found = false;
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                try {
                    JSONObject json = (JSONObject) parser.parse(line);
                    if (customerId.equals(json.get("user_id"))) {
                        found = true;
                        continue; // Skip this record
                    }
                } catch (ParseException e) {
                    // If parse fails, leave the line unchanged
                }
                lines.add(line);
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            return false;
        }
        if (!found) {
            System.out.println("Customer with id " + customerId + " not found.");
            return false;
        }
        try (FileWriter writer = new FileWriter("assignment/data/users.txt", false)) {
            for (String line : lines) {
                writer.write(line + System.lineSeparator());
            }
            return true;
        } catch (IOException e) {
            System.err.println("Error writing file: " + e.getMessage());
            return false;
        }
    }

    public class CustomerListResult {
        public List<Customer> customers;
        public int currentPage;
        public int totalPages;

        public CustomerListResult(List<Customer> customers, int currentPage, int totalPages) {
            this.customers = customers;
            this.currentPage = currentPage;
            this.totalPages = totalPages;
        }
    }

    public CustomerListResult getCustomerList(int pageNumber) {
        List<Customer> customers = new ArrayList<>();
        List<String> lines = new ArrayList<>();
        File file = new File("assignment/data/users.txt");
        // Read all records from the user file
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                lines.add(scanner.nextLine().trim());
            }
        } catch (IOException e) {
            System.err.println("Error reading file for customer list: " + e.getMessage());
        }
        // Filter only the records belonging to customers
        List<String> customerLines = new ArrayList<>();
        for (String line : lines) {
            if (line.isEmpty())
                continue;
            try {
                JSONObject json = (JSONObject) parser.parse(line);
                if ("customer".equals(json.get("user_role"))) {
                    customerLines.add(line);
                }
            } catch (ParseException e) {
                System.err.println("Error parsing line: " + e.getMessage());
            }
        }
        int totalCustomers = customerLines.size();
        int totalPages = (int) Math.ceil(totalCustomers / 10.0);
        if (pageNumber < 1) {
            pageNumber = 1;
        } else if (pageNumber > totalPages) {
            pageNumber = totalPages;
        }
        int start = (pageNumber - 1) * 10;
        int end = Math.min(start + 10, totalCustomers);
        
        for (int i = start; i < end; i++) {
            try {
                JSONObject json = (JSONObject) parser.parse(customerLines.get(i));
                String userId = (String) json.get("user_id");
                String userName = (String) json.get("user_name");
                String userPassword = (String) json.get("user_password"); // still encrypted
                String registerTime = (String) json.get("user_register_time");
                String email = (String) json.get("user_email");
                String mobile = (String) json.get("user_mobile");
                Customer customer = new Customer(userId, userName, userPassword, registerTime, "customer", email, mobile);
                customers.add(customer);
            } catch (ParseException e) {
                System.err.println("Error parsing customer record: " + e.getMessage());
            }
        }
        return new CustomerListResult(customers, pageNumber, totalPages);
    }

    public void deleteAllCustomers() {
        List<String> lines = new ArrayList<>();
        File file = new File("assignment/data/users.txt");
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty())
                    continue;
                try {
                    JSONObject json = (JSONObject) parser.parse(line);
                    if (!"customer".equals(json.get("user_role"))) {
                        lines.add(line);
                    }
                } catch (ParseException e) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        try (FileWriter writer = new FileWriter("data/users.txt", false)) {
            for (String line : lines) {
                writer.write(line + System.lineSeparator());
            }
        } catch (IOException e) {
            System.err.println("Error writing file: " + e.getMessage());
        }
    }

     public void exampleUsingHashMap() {
        // Instead of using a raw HashMap, we specify the types.
        // Here, we store keys and values as Strings.
        Map<String, String> customerDetails = new HashMap<>();
        
        // Adding customer attributes to the map.
        customerDetails.put("userId", "u_1234567890");
        customerDetails.put("userName", "exampleUser");
        customerDetails.put("user_email", "example@gmail.com");
        customerDetails.put("user_mobile", "0412345678");
        
        // You can now work with the parameterized map safely.
        System.out.println("Customer Details: " + customerDetails);
    }

    // ... Rest of your methods such as registerCustomer, updateProfile etc.
    
    // If you prefer, you can also place such code in a method like registerCustomer if there
    // is a need to build an intermediate map of key-value pairs before writing to file.
    
    /**
     * Example of how you could use a parameterized HashMap within your registration method.
     * (This is just an illustrative snippet and may not reflect the full registration workflow.)
     */
    public boolean registerCustomerUsingMap(String userId, String userName, String userPassword,
                                              String userEmail, String userMobile) {
        // Creating a parameterized HashMap to collect customer attributes.
        Map<String, String> customerData = new HashMap<>();
        customerData.put("user_id", userId);
        customerData.put("user_name", userName);
        customerData.put("user_password", userPassword); // This should be encrypted in practice.
        customerData.put("user_email", userEmail);
        customerData.put("user_mobile", userMobile);
        
        // Now you could convert this map to a JSON object, write to file, etc.
        System.out.println("CustomerData Map: " + customerData);
        
        // Return true to indicate success for this example.
        return true;
    }
}

