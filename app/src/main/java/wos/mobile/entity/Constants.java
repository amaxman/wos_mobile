package wos.mobile.entity;

public class Constants {
    public static final class RestConfig {
        //#region 全局配置
        public static final String restServer = "http://192.168.0.110";
        public static final int pageSize = 10;
        //#endregion

        public static final String login = "/rest/auth/login";
        public static final String login_token = "/rest/auth/loginBySession";
        public static final String logout = "/rest/auth/logout";
        public static final String auth_permission="/rest/auth/getAllPermission";
        public static final String work_order_page="/rest/workOrder/page";

    }
    public static final int pageSize = 10;
}
