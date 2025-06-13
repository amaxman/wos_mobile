package wos.mobile.ui.workOrder;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import wos.mobile.ActivityEx;
import wos.mobile.R;
import wos.mobile.annotation.AnnotateUtil;
import wos.mobile.annotation.BindView;
import wos.mobile.entity.EnumAction;
import wos.mobile.entity.JsonMsg;
import wos.mobile.entity.KeyValue;
import wos.mobile.entity.workOrder.WorkOrderRestEntity;
import wos.mobile.service.WorkOrderService;
import wos.mobile.util.BitmapUtil;
import wos.mobile.util.DateUtils;
import wos.mobile.util.StringUtil;
import wos.mobile.widget.CustomDatePickerDialogFragment;
import wos.mobile.widget.DateTimePickerDialogFragment;

public class WorkOrderEditActivity extends ActivityEx implements View.OnClickListener {

    //#region 变量

    //#endregion

    //#region 控件

    private WorkOrderRestEntity workOrderRestEntity = null;

    @BindView(id = "workOrderTitle")
    private EditText workOrderTitle;

    @BindView(id = "workOrderContent")
    private EditText workOrderContent;

    @BindView(id = "workOrderStartTime")
    private TextView workOrderStartTime;

    @BindView(id = "workOrderEndTime")
    private TextView workOrderEndTime;

    @BindView(id = "workOrderCateCode")
    private Spinner workOrderCateCode;

    @BindView(id = "workOrderLevelCode")
    private Spinner workOrderLevelCode;

    @BindView(id = "btnUploadImage")
    private Button btnUploadImage;


    //#endregion

    //#region 事件

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_order_edit);
        this.context = this;
        AnnotateUtil.initBindView(this);

        Intent intent = getIntent();

        String jsonString = intent.getStringExtra("data");
        if (StringUtil.isNotEmpty(jsonString)) {
            workOrderRestEntity = JSONObject.parseObject(jsonString, WorkOrderRestEntity.class);
        } else {
            btnUploadImage.setVisibility(View.GONE);
        }

        initView();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        //取消广播注册
        super.onPause();
    }

    public void initView() {
        title.setText(R.string.work_order);

        Bitmap bitmapBack = BitmapUtil.scale(BitmapFactory.decodeResource(getResources(), R.drawable.back_media), 64, 64);
        configReturnButton(btnReturn, R.string.Return, bitmapBack);

        Bitmap bitmapSave = BitmapUtil.scale(BitmapFactory.decodeResource(getResources(), R.drawable.save), 64, 64);
        configReturnButton(btnAction, R.string.save, bitmapSave);

        btnAction.setVisibility(View.VISIBLE);

        btnUploadImage.setPadding(0, 5, 0, 0);
        btnUploadImage.setCompoundDrawables(null, getButtonDrawable(R.drawable.find, 80, 80), null, null);

        setOnClickListener(Arrays.asList(btnAction,btnReturn,workOrderStartTime,workOrderEndTime, btnUploadImage), this);

        //#region 工单类别
        List<KeyValue<String,String>> cateCodeList=Arrays.asList(new KeyValue<>("","请选择"),new KeyValue<>("yes_no","是否"),new KeyValue<>("progress","进度"));
        ArrayAdapter<KeyValue<String,String>> arrAdapterCate = new ArrayAdapter<>(this, R.layout.item_selected, cateCodeList);
        workOrderCateCode.setAdapter(arrAdapterCate);
        workOrderCateCode.setPrompt("请选择：");
        if (!selectSpin(workOrderCateCode,cateCodeList,this.workOrderRestEntity.getCateCode())) {
            workOrderCateCode.setSelection(0);
        }
        //#endregion

        //#region 工单紧急程度
        List<KeyValue<String,String>> levelCodeList=Arrays.asList(new KeyValue<>("","请选择"),new KeyValue<>("emergency","紧急"),new KeyValue<>("important","重要"),new KeyValue<>("normal","普通"));
        ArrayAdapter<KeyValue<String,String>> arrAdapterLevel = new ArrayAdapter<>(this, R.layout.item_selected, levelCodeList);
        workOrderLevelCode.setAdapter(arrAdapterLevel);
        workOrderLevelCode.setPrompt("请选择：");
        if (!selectSpin(workOrderLevelCode,levelCodeList,this.workOrderRestEntity.getLevelCode())) {
            workOrderLevelCode.setSelection(0);
        }
        //#endregion

        showWorkOrder();
        if (labVersion != null) labVersion.setText(getVersionName());
    }

    /**
     * 选择Spin
     * @param spinner
     * @param list
     * @param code
     * @return
     */
    private boolean selectSpin(Spinner spinner,List<KeyValue<String,String>> list,String code) {
        for (int index=0;index<list.size();index++
        ) {
            KeyValue<String,String> item=list.get(index);
            if (StringUtil.equalsAnyIgnoreCase(item.getKey(),code)) {
                spinner.setSelection(index);
                return true;
            }
        }
        return false;
    }

    private Drawable getButtonDrawable(int resId, int width, int height) {
        Drawable drawable = null;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            drawable = context.getResources().getDrawable(
                    resId);
        } else {
            drawable = ContextCompat.getDrawable(context, resId);
        }

        drawable.setBounds(0, 0, width, height);
        return drawable;
    }



    @Override
    public void onClick(View view) {
        if (view == null) return;
        if (view == btnReturn) {
            finish();
        } else if (view == btnAction) {
            handler.sendEmptyMessage(EnumAction.edit.ordinal());
        } else if (view == btnUploadImage) {
            handler.sendEmptyMessage(EnumAction.launch.ordinal());
        } else if (view==workOrderStartTime) {
            pickTime(workOrderStartTime);
        } else if (view==workOrderEndTime) {
            pickTime(workOrderEndTime);
        }
    }

    private void pickTime(TextView txTime) {
        Date date;
        try {
            date= DateUtils.parseDate(txTime.getText().toString(),"yyyy-MM-dd HH:mm");
        } catch (Exception ex) {
            date=new Date();
        }
        showDataTimePick(date,onSelectedDateTimeListener(txTime));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showDataTimePick(Date date, DateTimePickerDialogFragment.OnSelectedDateTimeListener onSelectedDateTimeListener) {
        DateTimePickerDialogFragment dateTimePickerDialogFragment=new DateTimePickerDialogFragment();
        if (onSelectedDateTimeListener!=null) dateTimePickerDialogFragment.setOnSelectedDateListener(onSelectedDateTimeListener);
        Bundle bundle = new Bundle();
        if (date!=null) bundle.putSerializable(DateTimePickerDialogFragment.CURRENT_DATE, DateUtils.getCalendar(date));

        dateTimePickerDialogFragment.setArguments(bundle);
        dateTimePickerDialogFragment.show(getSupportFragmentManager(), CustomDatePickerDialogFragment.class.getSimpleName());
    }

    //#endregion

    //#region handler
    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message message) {
            EnumAction action = EnumAction.getByOrdinal(message.what);
            if (action == null) return;
            switch (action) {
                case edit:
                    saveData();
                    break;
                case edit_ui:
                    closeProgressDialog();
                    JsonMsg<String> jsonMsg = (JsonMsg<String>) message.obj;
                    showToast(jsonMsg.getMsg());
                    if (jsonMsg.isMsgType()) finish();
                    break;
                case launch:
//                    if (workOrderRestEntity ==null) return;
//                    Intent intentEdit = new Intent(context,ProductImagesActivity.class);
//                    intentEdit.putExtra("id", workOrderRestEntity.getId());
//                    startActivityForResult(intentEdit,0);
                    break;
                case query:
                    if (workOrderRestEntity == null) return;
                    loadWorkOrder();
                    break;
                case query_ui:
                    JsonMsg<WorkOrderRestEntity> jsonMsgProduct = (JsonMsg<WorkOrderRestEntity>) message.obj;
                    if (jsonMsgProduct == null) {
                        showToast("获取数据异常");
                        return;
                    }
                    workOrderRestEntity = jsonMsgProduct.getData();
                    showWorkOrder();

                    break;
                case toast:
                    showToast(String.valueOf(message.obj));
                    break;
            }
        }
    };


    //#endregion

    //#region 数据
    private void saveData() {
        if (workOrderRestEntity == null) {
            workOrderRestEntity = new WorkOrderRestEntity();
        }
        workOrderRestEntity.setTitle(getTextViewText(workOrderTitle));
        workOrderRestEntity.setContent(getTextViewText(workOrderContent));
        workOrderRestEntity.setStartTime(parseTime(getTextViewText(workOrderStartTime)));
        workOrderRestEntity.setEndTime(parseTime(getTextViewText(workOrderEndTime)));
        if (workOrderCateCode.getSelectedItem()!=null) {
            KeyValue<String,String> cateCodeSelect=(KeyValue<String, String>) workOrderCateCode.getSelectedItem();
            workOrderRestEntity.setCateCode(cateCodeSelect.getKey());
        }

        if (workOrderLevelCode.getSelectedItem()!=null) {
            KeyValue<String,String> levelCodeSelect=(KeyValue<String, String>) workOrderLevelCode.getSelectedItem();
            workOrderRestEntity.setLevelCode(levelCodeSelect.getKey());
        }

        showProgressDialog("正在保存...");

        new Thread(()->{
            WorkOrderService productRestService=new WorkOrderService();
            JsonMsg<String> jsonMsg=productRestService.save(workOrderRestEntity);
            handler.sendMessage(getMessage(EnumAction.edit_ui,jsonMsg));

        }).start();
    }

    private void loadWorkOrder() {
        if (this.workOrderRestEntity == null) return;

        new Thread(() -> {
//            ProductRestService productRestService=new ProductRestService();
//            JsonMsg<ProductRestEntity> jsonMsg=productRestService.get(workOrderRestEntity.getId());
//            handler.sendMessage(getMessage(EnumAction.get_id_success,jsonMsg));

        }).start();

    }

    private void showWorkOrder() {
        if (this.workOrderRestEntity == null) return;
        workOrderTitle.setText(this.workOrderRestEntity.getTitle());
        workOrderContent.setText(this.workOrderRestEntity.getContent());
        workOrderStartTime.setText(DateUtils.formatDate(this.workOrderRestEntity.getStartTime(),"yyyy-MM-dd HH:mm"));
        workOrderEndTime.setText(DateUtils.formatDate(this.workOrderRestEntity.getEndTime(),"yyyy-MM-dd HH:mm"));
    }
    //#endregion
}
