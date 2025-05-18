import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.json.simple.JSONObject;

public class Customer extends User {
    private String userEmail;
    private String userMobile;

    public Customer(String userId, String userName, String userPassword, String userRole, 
                    String userEmail, String userMobile) {
        super(userId, userName, userPassword, LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("dd-MM-yyyy_HH:mm:ss")), userRole);
        this.userEmail = userEmail;
        this.userMobile = userMobile;
    }

    public Customer(JSONObject json) {
        super(json.get("user_id").toString(), json.get("user_name").toString(), json.get("user_password").toString(), 
              json.get("user_register_time").toString(), json.get("user_role").toString());
        this.userEmail = json.get("user_email").toString();
        this.userMobile = json.get("user_mobile").toString();
    }

    @Override
    public String toString() {
        return String.format("{\"user_id\":\"%s\", \"user_name\":\"%s\", \"user_password\":\"%s\", " +
                             "\"user_register_time\":\"%s\", \"user_role\":\"%s\", " +
                             "\"user_email\":\"%s\", \"user_mobile\":\"%s\"}", 
                             getUserId(), getUserName(), getUserPassword(), getUserRegisterTime(), 
                             getUserRole(), userEmail, userMobile);
    }

    public String getUserEmail() { return userEmail; }
    public String getUserMobile() { return userMobile; }
}