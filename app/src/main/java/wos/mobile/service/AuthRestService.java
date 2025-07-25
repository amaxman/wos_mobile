package wos.mobile.service;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wos.mobile.R;
import wos.mobile.entity.Constants;
import wos.mobile.entity.JsonMsg;
import wos.mobile.entity.Property;
import wos.mobile.entity.auth.PermissionRestEntity;
import wos.mobile.entity.auth.UserSessionRestEntity;
import wos.mobile.util.HttpUtil;
import wos.mobile.util.StringUtil;

public class AuthRestService extends BasicRestService {
    /**
     * 通过用户名与密码登录
     *
     * @param userId       用户名
     * @param userPassword 密码
     * @return 用户登陆信息
     */
    public JsonMsg<UserSessionRestEntity> login(String userId, String userPassword) {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("userPassword", userPassword);
        String jsonString = HttpUtil.doPost(
                BasicRestService.restServer + Constants.RestConfig.login,
                map,
                "UTF-8");
        if (StringUtil.isEmpty(jsonString)) {
            return JsonMsg.error(getString(R.string.except_server_return_nothing));

        }
        JsonMsg<UserSessionRestEntity> jsonMsg = JSONObject.parseObject(jsonString, new TypeReference<JsonMsg<UserSessionRestEntity>>() {
        });
        if (jsonMsg == null) return JsonMsg.error(getString(R.string.server_parse_json_exception));
        return jsonMsg;
    }

    public JsonMsg<UserSessionRestEntity> login(String sessionId) {
        Map<String, Object> map = new HashMap<>();
        map.put("sessionId", sessionId);
        String jsonString = HttpUtil.doPost(
                BasicRestService.restServer + Constants.RestConfig.login_token,
                map,
                "UTF-8");
        if (StringUtil.isEmpty(jsonString)) {
            return JsonMsg.error(getString(R.string.except_server_return_nothing));

        }
        JsonMsg<UserSessionRestEntity> jsonMsg = JSONObject.parseObject(jsonString, new TypeReference<JsonMsg<UserSessionRestEntity>>() {
        });
        if (jsonMsg == null) return JsonMsg.error(getString(R.string.server_parse_json_exception));
        return jsonMsg;
    }

    /**
     * 获取功能编码
     *
     * @param sessionId 令牌
     * @return 用户授权列表
     */
    public JsonMsg<List<PermissionRestEntity>> getAccessFunc(String sessionId) {
        String url = BasicRestService.restServer + Constants.RestConfig.auth_permission;

        HashMap<String, Object> hm = new HashMap<>();
        hm.put("sessionId", sessionId);
        String jsonString = HttpUtil.doPost(
                url,
                hm,
                "UTF-8");
        if (StringUtil.isEmpty(jsonString)) {
            return JsonMsg.error(getString(R.string.except_server_return_nothing));
        }
        JsonMsg<List<PermissionRestEntity>> jsonMsg = JSONObject.parseObject(jsonString, new TypeReference<JsonMsg<List<PermissionRestEntity>>>() {
        });
        if (jsonMsg == null) return JsonMsg.error(getString(R.string.server_parse_json_exception));
        return jsonMsg;
    }

    public JsonMsg<Void> logout(String sessionId) {
        Map<String, Object> map = new HashMap<>();
        map.put("sessionId", sessionId);
        String jsonString = HttpUtil.doPost(
                BasicRestService.restServer + Constants.RestConfig.logout,
                map,
                "UTF-8");
        if (StringUtil.isEmpty(jsonString)) {
            return JsonMsg.error(getString(R.string.except_server_return_nothing));
        }
        JsonMsg<Void> jsonMsg = JSONObject.parseObject(jsonString, new TypeReference<JsonMsg<Void>>() {
        });
        if (jsonMsg == null) return JsonMsg.error(getString(R.string.server_parse_json_exception));
        return jsonMsg;
    }

}
