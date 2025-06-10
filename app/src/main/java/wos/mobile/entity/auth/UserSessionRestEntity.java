package wos.mobile.entity.auth;

import com.alibaba.fastjson.annotation.JSONField;

public class UserSessionRestEntity {
    /**
     * 令牌
     */
    @JSONField(name = "session_id")
    private String sessionId;

    /**
     * 登陆用户名
     */
    @JSONField(name = "username")
    private String userName;

    /**
     * 用户全名
     */
    @JSONField(name = "full_name")
    private String fullName;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
