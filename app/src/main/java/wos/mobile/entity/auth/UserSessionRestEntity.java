package wos.mobile.entity.auth;

import com.alibaba.fastjson.annotation.JSONField;

public class UserSessionRestEntity {
    /**
     * 令牌
     */
    @JSONField(name = "sessionId")
    private String sessionId;

    /**
     * 登陆用户名
     */
    @JSONField(name = "staffName")
    private String staffName;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }
}
