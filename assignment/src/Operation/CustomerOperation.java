package Operation;
import Model.Customer;

import org.json.simple.JSONValue;
import java.util.LinkedHashMap;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


/**
 * The {@code CustomerOperation} class provides operations related to customer management,
 * including customer registration, validation of email and mobile fields, checking for the existence
 * of a username, and updating customer profiles. This class is implemented as a singleton to guarantee
 * that all customer-related operations are handled by a single instance.
 */
public class CustomerOperation {
    
    /** Singleton instance of CustomerOperation. */
    private static CustomerOperation instance;
    
    /** JSON parser for handling JSON data from the users file. */
    private JSONParser parser;
    
    /**
     * Private constructor to enforce singleton pattern.
     * Initializes the JSON parser.
     */
    private CustomerOperation() {
        parser = new JSONParser();
    }
    
    /**
     * Retrieves the singleton instance of {@code CustomerOperation}.
     * If an instance does not exist, one is created.
     *
     * @return the singleton instance of CustomerOperation.
     */
    public static CustomerOperation getInstance() {
        if (instance == null) {
            instance = new CustomerOperation();
        }
        return instance;
    }
    
    /**
     * Validates the provided email address using a regular expression.
     *
     * @param userEmail the email address to validate.
     * @return {@code true} if the email is non-null and matches the expected format; {@code false} otherwise.
     */
    public boolean validateEmail(String userEmail) {
        return userEmail != null && userEmail.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }
    
    /**
     * Validates the provided mobile number.
     * <p>
     * Mobile numbers are expected to start with "04" or "03" followed by 8 digits.
     * </p>
     *
     * @param userMobile the mobile number to validate.
     * @return {@code true} if the mobile number is valid; {@code false} otherwise.
     */
    public boolean validateMobile(String userMobile) {
        return userMobile != null && userMobile.matches("^(04|03)\\d{8}$");
    }
    
    /**
     * Registers a new customer with the given details.
     * <p>
     * The registration includes validating the username (to ensure it is unique),
     * email, and mobile number. The customer password is encrypted using the operations
     * provided by {@link UserOperation}. The customer is then written to the users file.
     * </p>
     *
     * @param userName     the desired username.
     * @param userPassword the plain text password.
     * @param userEmail    the customer's email address.
     * @param userMobile   the customer's mobile number.
     * @return {@code true} if registration is successful; {@code false} otherwise.
     */
    public boolean registerCustomer(String userName, String userPassword, String userEmail, String userMobile) {
        if (checkUsernameExist(userName))
            return false;
        if (!validateEmail(userEmail) || !validateMobile(userMobile))
            return false;
    
        String userId = generateUniqueUserId();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH:mm:ss");
        String registerTime = LocalDateTime.now().format(formatter);
    
        // Use a LinkedHashMap to store key/value pairs in a fixed order.
        LinkedHashMap<String, Object> customerObj = new LinkedHashMap<>();
        customerObj.put("user_id", userId);
        customerObj.put("user_name", userName);
        customerObj.put("user_password", UserOperation.getInstance().encryptPassword(userPassword));
        customerObj.put("user_register_time", registerTime);
        customerObj.put("user_role", "customer");
        customerObj.put("user_email", userEmail);
        customerObj.put("user_mobile", userMobile);
    
        // Define the key order you want.
        List<String> keyOrder = List.of(
            "user_id",
            "user_name",
            "user_password",
            "user_register_time",
            "user_role",
            "user_email",
            "user_mobile"
        );
    
        // Build your JSON string with the helper.
        String customerJSONStr = toOrderedJSONString(customerObj, keyOrder);
    
        // Write the JSON string to the file.
        writeUserToFile(customerJSONStr);
        return true;
    }


    /**
    * Converts the given LinkedHashMap to a JSON string with keys output in the specified order.
    *
    * @param orderedMap the map containing JSON keys and values.
    * @param keyOrder   the list of keys in the order you want them printed.
    * @return a JSON formatted string with the keys in the specified order.
    */
    private String toOrderedJSONString(LinkedHashMap<String, Object> orderedMap, List<String> keyOrder) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        boolean first = true;
        for (String key : keyOrder) {
            if (orderedMap.containsKey(key)) {
                if (!first) {
                    sb.append(",");
                }
            sb.append("\"").append(key).append("\":");
            sb.append(JSONValue.toJSONString(orderedMap.get(key)));
            first = false;
        }
    }
        sb.append("}");
        return sb.toString();
    }


    /**
     * Writes a JSON object representing a user to the users file ("assignment/data/users.txt").
     * <p>
     * This method opens the file in append mode and writes the JSON-formatted string followed by a new line.
     * </p>
     *
     * @param userObj the JSON object containing the user's details.
     */
    private void writeUserToFile(String jsonStr) {
        File file = new File("assignment/data/users.txt");
        try (FileWriter writer = new FileWriter(file, true)) {
            writer.write(jsonStr + System.lineSeparator());
        } catch (IOException e) {
            System.err.println("Error writing user to file: " + e.getMessage());
        }
    }

    
    /**
     * Checks if a username already exists in the users file.
     * <p>
     * The file "assignment/data/users.txt" is scanned line by line and parsed as JSON.
     * If any record has a matching "user_name" field, the method returns {@code true}.
     * </p>
     *
     * @param userName the username to search for.
     * @return {@code true} if the username exists; {@code false} otherwise.
     */
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
                    // Ignore parse errors and continue through the file.
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Generates a unique user ID for a new customer.
     * <p>
     * The implementation uses the current system time in milliseconds to guarantee uniqueness.
     * </p>
     *
     * @return a unique user ID string starting with "u_".
     */
    private String generateUniqueUserId() {
        // Example: "u_" + current timestamp in milliseconds.
        return "u_" + System.currentTimeMillis();
    }
    
/**
 * Updates a single attribute of a customer's profile.
 * <p>
 * Rather than parsing and re-serializing the JSON (which can change the format),
 * this method uses regex to update only the target field in the original line.
 * Supported attributes: "user_name", "user_password", "user_email", and "user_mobile".
 * </p>
 *
 * @param attributeName  the attribute to update.
 * @param value          the new value.
 * @param customerObject the customer whose profile is being updated.
 * @return {@code true} if the update was successful; {@code false} otherwise.
 */
public boolean updateProfile(String attributeName, String value, Customer customerObject) {
    if (customerObject == null) {
        System.out.println("Customer does not exist.");
        return false;
    }

    List<String> lines = new ArrayList<>();
    File file = new File("assignment/data/users.txt");
    boolean found = false;

    try (Scanner scanner = new Scanner(file)) {
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String trimmedLine = line.trim();
            if (trimmedLine.isEmpty()) {
                lines.add(line);
                continue;
            }
            try {
                // Try to parse the record, so we can check the user_id.
                JSONObject json = (JSONObject) parser.parse(trimmedLine);
                // If this is the record to update:
                if (customerObject.getUserId().equals(json.get("user_id"))) {
                    found = true;
                    String updatedLine = line;
                    // Depending on which attribute we're updating, perform validation and regex replacement.
                    if ("user_name".equals(attributeName)) {
                        if (!UserOperation.getInstance().validateUsername(value)) {
                            System.out.println("Invalid username.");
                            return false;
                        }
                        // Replace the existing user_name value
                        updatedLine = line.replaceFirst("\"user_name\":\"[^\"]*\"", "\"user_name\":\"" + value + "\"");
                    } else if ("user_password".equals(attributeName)) {
                        if (!UserOperation.getInstance().validatePassword(value)) {
                            System.out.println("Invalid password.");
                            return false;
                        }
                        String encrypted = UserOperation.getInstance().encryptPassword(value);
                        updatedLine = line.replaceFirst("\"user_password\":\"[^\"]*\"", "\"user_password\":\"" + encrypted + "\"");
                    } else if ("user_email".equals(attributeName)) {
                        if (!validateEmail(value)) {
                            System.out.println("Invalid email.");
                            return false;
                        }
                        updatedLine = line.replaceFirst("\"user_email\":\"[^\"]*\"", "\"user_email\":\"" + value + "\"");
                    } else if ("user_mobile".equals(attributeName)) {
                        if (!validateMobile(value)) {
                            System.out.println("Invalid mobile number.");
                            return false;
                        }
                        updatedLine = line.replaceFirst("\"user_mobile\":\"[^\"]*\"", "\"user_mobile\":\"" + value + "\"");
                    } else {
                        System.out.println("Attribute not allowed for update.");
                        return false;
                    }
                    lines.add(updatedLine);
                } else {
                    // For records not matching the target user, keep the line unchanged.
                    lines.add(line);
                }
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

    // Write all records (with the updated record) back to the file.
    try (FileWriter writer = new FileWriter(file, false)) {
        for (String l : lines) {
            writer.write(l + System.lineSeparator());
        }
        return true;
    } catch (IOException e) {
        System.err.println("Error writing file: " + e.getMessage());
        return false;
    }
}


    /**
     * Deletes a customer record by their user ID.
     * <p>
     * The method reads all user records from "assignment/data/users.txt", removes
     * the matching customer record, and writes the remaining records back.
     * </p>
     *
     * @param customerId the unique identifier of the customer to delete.
     * @return {@code true} if the customer was found and deleted; {@code false} otherwise.
     */
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
                    // Ignore parse errors and keep the record unchanged.
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

    /**
     * A helper class that encapsulates paginated customer list results.
     */
    public static class CustomerListResult {
        /** List of customers for the current page. */
        public List<Customer> customers;
        /** Current page number. */
        public int currentPage;
        /** Total number of pages available. */
        public int totalPages;

        /**
         * Constructs a new {@code CustomerListResult} instance.
         *
         * @param customers the list of customers for the current page.
         * @param currentPage the current page number.
         * @param totalPages the total number of pages available.
         */
        public CustomerListResult(List<Customer> customers, int currentPage, int totalPages) {
            this.customers = customers;
            this.currentPage = currentPage;
            this.totalPages = totalPages;
        }
    }

    /**
     * Retrieves a paginated list of customers from the users file.
     * <p>
     * The method filters only customers (users with the role "customer") and
     * returns a paginated result, displaying up to 10 customers per page.
     * </p>
     *
     * @param pageNumber the page number to retrieve.
     * @return a {@code CustomerListResult} encapsulating the paginated customer list.
     */
    public CustomerListResult getCustomerList(int pageNumber) {
        List<Customer> customers = new ArrayList<>();
        List<String> lines = new ArrayList<>();
        File file = new File("assignment/data/users.txt");

        // Read all records from the users file
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                lines.add(scanner.nextLine().trim());
            }
        } catch (IOException e) {
            System.err.println("Error reading file for customer list: " + e.getMessage());
        }

        // Filter out records belonging to customers
        List<String> customerLines = new ArrayList<>();
        for (String line : lines) {
            if (line.isEmpty()) continue;
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

    /**
     * Deletes all customer records while preserving other user accounts.
     * <p>
     * This method filters out only the users with a role of "customer" and retains
     * all other user records in the users file.
     * </p>
     */
    public void deleteAllCustomers() {
        List<String> lines = new ArrayList<>();
        File file = new File("assignment/data/users.txt");

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;
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

        try (FileWriter writer = new FileWriter("assignment/data/users.txt", false)) {
            for (String line : lines) {
                writer.write(line + System.lineSeparator());
            }
        } catch (IOException e) {
            System.err.println("Error writing file: " + e.getMessage());
        }
        }
    }



