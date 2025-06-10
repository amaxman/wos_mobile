package wos.mobile.entity;

public class Constants {
    public static final class RestConfig {
        //#region 全局配置
        public static final String restServer = "http://192.168.0.29:8080";
        public static final int pageSize = 10;
        //#endregion

        public static final String login = "/rest/auth/login";
        public static final String auth_permission="/rest/system/mobileAccess/permission/";

    }
}
