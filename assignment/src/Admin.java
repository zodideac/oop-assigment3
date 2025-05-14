import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Admin{
    protected String userID;
    protected String userName;
    protected String userPassword;
    protected String userRegisterTime;
    protected String userRole;


    public Admin(String userID, String userName, String userPassword, String userRegisterTime, String userRole) {
        this.userID = userID;
        this.userName = userName;
        this.userPassword = userPassword;
        this.userRegisterTime = userRegisterTime;
        this.userRole = userRole;
    }

    public String userID() {
        return userID;
    }

    public String userName(){
        return userName;
    }

    public String userPassword() {
        return userPassword;
    }

    public double userRegisterTime() {
        return userRegisterTime;
    }

    public String userRole() {
        return userRole;
    }
    public Admin(){
        this.userID = "u_1234567890";
        this.userName = "Default UserName";
        this.userPassword = "Default Password";
        this.userRegisterTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy_HH:mm:ss"));
        this.userRole = "Admin";

    }

    @Override
    public String toString() {
        return "UserID: " + userID + "\nuserName: " + userName + "\nuserPassword: " + userPassword
                + "\nuserRegisterTime: " + userRegisterTime + "\nuserRole:" + userRole;
    }
}
