package wos.mobile.entity.auth;

public class UserRestEntity {
    private String userCode;
    private String userName;

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String toString() {
        return String.valueOf(this.userName);
    }
}
