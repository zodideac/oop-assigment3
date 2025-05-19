package Model;
public abstract class User {
    protected String userId;
    protected String userName;
    protected String userPassword; 
    protected String userRegisterTime;
    protected String userRole; 

    public User(String userId, String userName, String userPassword, 
                String userRegisterTime, String userRole) {
        this.userId = userId;
        this.userName = userName;
        this.userPassword = userPassword;
        this.userRegisterTime = userRegisterTime;
        this.userRole = userRole;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public String getUserRegisterTime() {
        return userRegisterTime;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public void setUserRegisterTime(String userRegisterTime) {
        this.userRegisterTime = userRegisterTime;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }
    

    public User() {
        this.userId = "u_0000000000";
        this.userName = "defaultUser";
        this.userPassword = "defaultPassword";
        this.userRegisterTime = "01-01-1970_00:00:00";
        this.userRole = "customer";
    }

    @Override
    public String toString() {
        return String.format("{\"user_id\":\"%s\",\"user_name\":\"%s\",\"user_password\":\"%s\"," +
                             "\"user_register_time\":\"%s\",\"user_role\":\"%s\"}",
                             userId, userName, userPassword, userRegisterTime, userRole);
    }
}