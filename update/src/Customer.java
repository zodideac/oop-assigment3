import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Customer extends User{
    protected String userID;
    protected String userName;
    protected String userPassword;
    protected String userRegisterTime;
    protected String userRole;
    protected String userEmail;
    protected String userMobile;

    public Customer(String userID, String userName, String userPassword, String userRegisterTime, String userRole, String userEmail,String userMobile) {
        this.userID = userID;
        this.userName = userName;
        this.userPassword = userPassword;
        this.userRegisterTime = userRegisterTime;
        this.userRole = userRole;
        this.userEmail = userEmail;
        this.userMobile = userMobile;
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

    public String userRegisterTime() {
        return userRegisterTime;
    }

    public String userRole() {
        return userRole;
    }
      public String userEmail(){
        return userEmail;
    }
       public String userMobile(){
        return userMobile;
    }

    public Customer() {
        this.userID = "u_1234567890";
        this.userName = "Default UserName";
        this.userPassword = "Default Password";
        this.userRegisterTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy_HH:mm:ss"));
        this.userRole = "Customer";
        this.userEmail = "User email";
        this.userMobile = "User mobile";
    }

    @Override
    public String toString() {
        return "UserID: " + userID + "\nuserName: " + userName + "\nuserPassword: " + userPassword
                + "\nuserRegisterTime: " + userRegisterTime + "\nuserRole:" + userRole + "\nuserEmail" + userEmail + "\nuserMobile" +userMobile;
    }
}
