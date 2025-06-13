package wos.mobile.service;

import android.graphics.Bitmap;
import android.os.Build;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wos.mobile.R;
import wos.mobile.entity.Constants;
import wos.mobile.entity.FileUploadRestEntity;
import wos.mobile.entity.JsonMsg;
import wos.mobile.entity.Page;
import wos.mobile.entity.Property;
import wos.mobile.entity.workOrder.WorkOrderRestEntity;
import wos.mobile.entity.workOrder.WorkOrderStaffRestEntity;
import wos.mobile.util.HttpUtil;
import wos.mobile.util.StringUtil;
import wos.mobile.util.FileIOUtil;

public class WorkOrderService extends BasicRestService{
    //#region 工单
    public JsonMsg<Page<WorkOrderRestEntity>> find(String keyword, int pageNo, int pageSize) {
        Map<String,Object> map=new HashMap<>();
        map.put("sessionId", Property.sessionId);
        map.put("keyword", keyword);
        map.put("pageNo", pageNo);
        map.put("pageSize", pageSize);
        String jsonString = HttpUtil.doPost(
                BasicRestService.restServer+ Constants.RestConfig.work_order_page,
                map,
                "UTF-8");
        if (StringUtil.isEmpty(jsonString)) {
            return JsonMsg.error(getString(R.string.server_return_nothing));
        }
        JsonMsg<Page<WorkOrderRestEntity>> jsonMsg= JSONObject.parseObject(jsonString,new TypeReference<>() {});
        if (jsonMsg==null) return JsonMsg.error(getString(R.string.server_parse_json_exception));
        return jsonMsg;
    }

    /**
     * 删除工单
     * @param id 工单标识
     * @return 无
     */
    public JsonMsg<Void> delete(String id) {
        if (StringUtil.isEmpty(id)) return error("删除标识不允许为空");
        Map<String,Object> map=new HashMap<>();
        map.put("sessionId", Property.sessionId);
        map.put("id", id);
        String jsonString = HttpUtil.doPost(
                BasicRestService.restServer+ Constants.RestConfig.work_order_delete,
                map,
                "UTF-8");
        if (StringUtil.isEmpty(jsonString)) {
            return JsonMsg.error(getString(R.string.server_return_nothing));
        }
        JsonMsg<Void> jsonMsg= JSONObject.parseObject(jsonString,new TypeReference<>() {});
        if (jsonMsg==null) return JsonMsg.error(getString(R.string.server_parse_json_exception));
        return jsonMsg;

    }

    /**
     * 保存工单信息
     * @param workOrderRestEntity 工单
     * @return 工单标识
     */
    public JsonMsg<String> save(WorkOrderRestEntity workOrderRestEntity) {
        if (workOrderRestEntity==null) return error("工单不允许为空");
        String jsonString = HttpUtil.doJsonPost(
                BasicRestService.restServer+ String.format(Constants.RestConfig.work_order_save,Property.sessionId),
                JSON.toJSONString(workOrderRestEntity));
        if (StringUtil.isEmpty(jsonString)) {
            return JsonMsg.error(getString(R.string.server_return_nothing));
        }
        JsonMsg<String> jsonMsg= JSONObject.parseObject(jsonString,new TypeReference<>() {});
        if (jsonMsg==null) return JsonMsg.error(getString(R.string.server_parse_json_exception));
        return jsonMsg;

    }

    /**
     * 获取图片大小
     * @param bitmap 图片
     * @return 图片大小
     */
    private int getBitmapSize(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {    //API 19
            return bitmap.getAllocationByteCount();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {//API 12
            return bitmap.getByteCount();
        }
        // 在低版本中用一行的字节x高度
        return bitmap.getRowBytes() * bitmap.getHeight();                //earlier version
    }

    /**
     * 上传工单图片
     * @param id 工单标识
     * @param bitmap 图片
     * @param fileName 文件名称
     * @return 文件上传数据
     */
    public JsonMsg<FileUploadRestEntity> uploadImage(String id, Bitmap bitmap, String fileName) {
        if (StringUtil.isEmpty(id)) return JsonMsg.error("商品标识不允许为空");
        if (bitmap==null) return JsonMsg.error("商品图片不存在");
        String url=BasicRestService.restServer+String.format(Constants.RestConfig.work_order_imageUpload,Property.sessionId,id);
        try {
            String fileMD5 = FileIOUtil.getBitmapMD5(bitmap);
            if (StringUtil.isEmpty(fileMD5)) return JsonMsg.error("获取文件MD5失败");

            long fileLength=getBitmapSize(bitmap);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            InputStream isBm = new ByteArrayInputStream(baos.toByteArray());

            DataInputStream in = new DataInputStream(isBm);
            byte[] bufferOut = new byte[(int)fileLength];

            int bytes = 0;

            bytes=in.read(bufferOut);

            long timeMSecond=System.currentTimeMillis();
            HttpURLConnection con=HttpUtil.getHttpURLConnection(url,timeMSecond);
            OutputStream out=HttpUtil.getOutStream(con,fileName,fileMD5,fileLength,0,(int)fileLength,timeMSecond);
            if (bytes==bufferOut.length) {
                out.write(bufferOut);
            } else {
                byte[] bs= Arrays.copyOfRange(bufferOut,  0 ,  bytes );
                out.write(bs);
            }
            String BOUNDARY = "----------" + timeMSecond;
            byte[] foot = ("\r\n--" + BOUNDARY + "--\r\n").getBytes(StandardCharsets.UTF_8);// 定义最后数据分隔线
            out.write(foot);
            out.flush();
            out.close();
            String jsonString=HttpUtil.getOutStreamResult(con);
            if (StringUtil.isEmpty(jsonString)) return JsonMsg.error("服务器未返回消息");
            JsonMsg<FileUploadRestEntity> jsonMsg=JSONObject.parseObject(jsonString,new TypeReference<>() {});
            if (jsonMsg==null) return JsonMsg.error("解析服务器消息异常");
            return jsonMsg;

        } catch (Exception ex) {
            return JsonMsg.error("异常"+ex.getMessage());
        }
    }

    /**
     * 保存工单员工
     * @param workOrderStaffRestEntity 工单员工信息
     * @return 保存结果及标识
     */
    public JsonMsg<String> saveStaff(WorkOrderStaffRestEntity workOrderStaffRestEntity) {
        if (workOrderStaffRestEntity==null) return error("员工不允许为空");
        Map<String,Object> map=new HashMap<>();
        map.put("sessionId", Property.sessionId);
        String jsonString = HttpUtil.doJsonPost(
                BasicRestService.restServer+ String.format(Constants.RestConfig.work_order_saveStaff,Property.sessionId),
                JSON.toJSONString(workOrderStaffRestEntity));
        if (StringUtil.isEmpty(jsonString)) {
            return JsonMsg.error(getString(R.string.server_return_nothing));
        }
        JsonMsg<String> jsonMsg= JSONObject.parseObject(jsonString,new TypeReference<>() {});
        if (jsonMsg==null) return JsonMsg.error(getString(R.string.server_parse_json_exception));
        return jsonMsg;
    }

    /**
     * 删除工单员工
     * @param id 工单员工标识
     * @return 删除结果
     */
    public JsonMsg<Void> deleteStaff(String id) {
        if (StringUtil.isEmpty(id)) return error("删除标识不允许为空");
        Map<String,Object> map=new HashMap<>();
        map.put("sessionId", Property.sessionId);
        map.put("id", id);
        String jsonString = HttpUtil.doPost(
                BasicRestService.restServer+ Constants.RestConfig.work_order_deleteStaff,
                map,
                "UTF-8");
        if (StringUtil.isEmpty(jsonString)) {
            return JsonMsg.error(getString(R.string.server_return_nothing));
        }
        JsonMsg<Void> jsonMsg= JSONObject.parseObject(jsonString,new TypeReference<>() {});
        if (jsonMsg==null) return JsonMsg.error(getString(R.string.server_parse_json_exception));
        return jsonMsg;
    }
    //#endregion

    //#region 工单执行嗯
    public JsonMsg<List<WorkOrderStaffRestEntity>> staffList(String woId) {
        Map<String,Object> map=new HashMap<>();
        map.put("sessionId", Property.sessionId);
        map.put("woId", woId);
        String jsonString = HttpUtil.doPost(
                BasicRestService.restServer+ Constants.RestConfig.work_order_staff_list,
                map,
                "UTF-8");
        if (StringUtil.isEmpty(jsonString)) {
            return JsonMsg.error(getString(R.string.server_return_nothing));
        }
        JsonMsg<List<WorkOrderStaffRestEntity>> jsonMsg= JSONObject.parseObject(jsonString,new TypeReference<>() {});
        if (jsonMsg==null) return JsonMsg.error(getString(R.string.server_parse_json_exception));
        return jsonMsg;
    }

    public JsonMsg<Page<WorkOrderStaffRestEntity>> staffPage(String keyword,String title,String content,String startTime,String endTime,String workStatus, int pageNo, int pageSize) {
        Map<String,Object> map=new HashMap<>();
        map.put("sessionId", Property.sessionId);
        if (StringUtil.isNotEmpty(keyword)) map.put("keyword", keyword);
        if (StringUtil.isNotEmpty(title)) map.put("title", title);
        if (StringUtil.isNotEmpty(content)) map.put("content", content);
        if (StringUtil.isNotEmpty(startTime)) map.put("startTime", startTime);
        if (StringUtil.isNotEmpty(endTime)) map.put("endTime", endTime);
        if (StringUtil.isNotEmpty(workStatus)) map.put("workStatus", workStatus);
        map.put("pageNo", pageNo);
        map.put("pageSize", pageSize);
        String jsonString = HttpUtil.doPost(
                BasicRestService.restServer+ Constants.RestConfig.work_order_staff_page,
                map,
                "UTF-8");
        if (StringUtil.isEmpty(jsonString)) {
            return JsonMsg.error(getString(R.string.server_return_nothing));
        }
        JsonMsg<Page<WorkOrderStaffRestEntity>> jsonMsg= JSONObject.parseObject(jsonString,new TypeReference<>() {});
        if (jsonMsg==null) return JsonMsg.error(getString(R.string.server_parse_json_exception));
        return jsonMsg;
    }

    public JsonMsg<Void> staffSave(String id,int progress) {
        Map<String,Object> map=new HashMap<>();
        map.put("sessionId", Property.sessionId);
        map.put("id", id);
        map.put("progress", progress);
        String jsonString = HttpUtil.doPost(
                BasicRestService.restServer+ Constants.RestConfig.work_order_staff_save,
                map,
                "UTF-8");
        if (StringUtil.isEmpty(jsonString)) {
            return JsonMsg.error(getString(R.string.server_return_nothing));
        }
        JsonMsg<Void> jsonMsg= JSONObject.parseObject(jsonString,new TypeReference<>() {});
        if (jsonMsg==null) return JsonMsg.error(getString(R.string.server_parse_json_exception));
        return jsonMsg;
    }
    //#endregion
}
