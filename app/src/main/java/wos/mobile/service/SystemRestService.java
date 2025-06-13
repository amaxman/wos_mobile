package wos.mobile.service;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wos.mobile.R;
import wos.mobile.entity.Constants;
import wos.mobile.entity.JsonMsg;
import wos.mobile.entity.Property;
import wos.mobile.entity.auth.UserRestEntity;
import wos.mobile.util.HttpUtil;
import wos.mobile.util.StringUtil;

public class SystemRestService extends BasicRestService {
    /**
     * 获取所有用户信息
     *
     * @return 用户信息
     */
    public JsonMsg<List<UserRestEntity>> getAllUser() {
        Map<String, Object> map = new HashMap<>();
        map.put("sessionId", Property.sessionId);

        String jsonString = HttpUtil.doPost(
                BasicRestService.restServer + Constants.RestConfig.system_getAllUser,
                map,
                "UTF-8");
        if (StringUtil.isEmpty(jsonString)) {
            return JsonMsg.error(getString(R.string.server_return_nothing));
        }
        JsonMsg<List<UserRestEntity>> jsonMsg = JSONObject.parseObject(jsonString, new TypeReference<>() {
        });
        if (jsonMsg == null) return JsonMsg.error(getString(R.string.server_parse_json_exception));
        return jsonMsg;
    }
}
