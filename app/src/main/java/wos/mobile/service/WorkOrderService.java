package wos.mobile.service;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

import java.util.HashMap;
import java.util.Map;

import wos.mobile.R;
import wos.mobile.entity.Constants;
import wos.mobile.entity.JsonMsg;
import wos.mobile.entity.Page;
import wos.mobile.entity.Property;
import wos.mobile.entity.workOrder.WorkOrderRestEntity;
import wos.mobile.util.HttpUtil;
import wos.mobile.util.StringUtil;

public class WorkOrderService extends BasicRestService{
    public JsonMsg<Page<WorkOrderRestEntity>> find(String keyword, int limit, int offset) {
        Map<String,Object> map=new HashMap<>();
        map.put("sessionId", Property.sessionId);
        map.put("search", keyword);
        map.put("limit", limit);
        map.put("offset", offset);
        String jsonString = HttpUtil.doPost(
                BasicRestService.restServer+ Constants.RestConfig.work_order_find,
                map,
                "UTF-8");
        if (StringUtil.isEmpty(jsonString)) {
            return JsonMsg.error(getString(R.string.server_return_nothing));
        }
        JsonMsg<Page<WorkOrderRestEntity>> jsonMsg= JSONObject.parseObject(jsonString,new TypeReference<JsonMsg<Page<WorkOrderRestEntity>>>() {});
        if (jsonMsg==null) return JsonMsg.error(getString(R.string.server_parse_json_exception));
        return jsonMsg;
    }
}
