public class Costumer{
    protected String userID;
    protected String userName;
    protected String userPassword;
    protected String userRegisterTime;
    protected String userRole;
    protected String userEmail;
    protected String userMobile;

    public Costumer(String userID, String userName, String userPassword, String userRegisterTime, String userRole, String userEmail,String userMobile){
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

    public double userRegisterTime() {
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


    public User(){
        this.userID = "u_1234567890";
        this.userName = userName
        this.userPassword = userPassword
        this.userRegisterTime = userRegisterTime
        this.userRole = "Customer";
        this.userEmail = userEmail;
        this.userMobile = userMobile;

    }

    @Override
    public String toString() {
        return "UserID: " + userID + "\nuserName: " + userName + "\nuserPassword: " + userPassword
                + "\nuserRegisterTime: " + userRegisterTime + "\nuserRole:" + userRole + "\nuserEmail" + userEmail + "\nuserMobile" +userMobile;
    }
}
