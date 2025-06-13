package wos.mobile.entity;

public class Constants {
    public static final class RestConfig {
        //#region 全局配置
        public static final String restServer = "http://192.168.0.29";
        public static final int pageSize = 10;
        //#endregion

        public static final String login = "/rest/auth/login";
        public static final String login_token = "/rest/auth/loginBySession";
        public static final String logout = "/rest/auth/logout";
        public static final String system_getAllUser = "/rest/system/user/getAllUser";
        public static final String auth_permission="/rest/auth/getAllPermission";
        public static final String work_order_page="/rest/workOrder/page";
        public static final String work_order_delete="/rest/workOrder/delete";
        public static final String work_order_save="/rest/workOrder/save/%s";
        public static final String work_order_imageUpload="/rest/workOrder/uploadImages/%s/%s";
        public static final String work_order_saveStaff="/rest/workOrder/saveStaff/%s";
        public static final String work_order_deleteStaff="/rest/workOrder/deleteStaff";
        public static final String work_order_staff_list="/rest/workOrder/staffList";
        public static final String work_order_staff_page="/rest/workOrder/staffPage";
        public static final String work_order_staff_save="/rest/workOrder/staffSave";

    }
    public static final int pageSize = 10;
}
