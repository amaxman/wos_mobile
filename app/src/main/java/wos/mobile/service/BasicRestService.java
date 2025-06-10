package wos.mobile.service;


import wos.mobile.entity.JsonMsg;
import wos.mobile.entity.Property;

public abstract class BasicRestService {

    public static String restServer;

    protected String getString(int resId) {
        return Property.context.getString(resId);
    }

    /**
     * 返回JSON错误消息
     *
     * @param msg 错误消息内容
     * @param <T> 范型
     * @return JsonMsg
     */
    public static <T> JsonMsg<T> error(String msg) {
        return JsonMsg.error(msg);
    }

    /**
     * 返回JSON错误消息
     * @param resId 错误消息内容资源标识
     * @return JsonMsg
     * @param <T> 范型
     */
    public static <T> JsonMsg<T> error(int resId) {
        return JsonMsg.error(Property.context.getString(resId));
    }
}
