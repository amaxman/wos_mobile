package wos.mobile.ui.workOrder;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.Nullable;

import java.util.Arrays;
import java.util.List;

import wos.mobile.ActivityEx;
import wos.mobile.R;
import wos.mobile.annotation.AnnotateUtil;
import wos.mobile.annotation.BindView;
import wos.mobile.entity.EnumAction;
import wos.mobile.entity.JsonMsg;
import wos.mobile.entity.KeyValue;
import wos.mobile.entity.auth.UserRestEntity;
import wos.mobile.entity.workOrder.WorkOrderStaffRestEntity;
import wos.mobile.service.SystemRestService;
import wos.mobile.service.WorkOrderService;
import wos.mobile.util.BitmapUtil;
import wos.mobile.util.StringUtil;
import wos.mobile.widget.BasicDialogFragment;

public class WorkOrderStaffAddFragment extends BasicDialogFragment implements View.OnClickListener {

    //#region 变量
    private List<UserRestEntity> list;
    private String woId;
    //#endregion

    //#region 控件


    @BindView(id = "staffSelector")
    private Spinner staffSelector;


    //#endregion

    //#region 事件

    public WorkOrderStaffAddFragment(String id, Context context) {
        super(context);
        this.woId = id;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=getCreateView(inflater,container,savedInstanceState,R.layout.activity_work_order_staff_add);
        return view;
    }



    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AnnotateUtil.initBindView(this, view, context.getPackageName());
        initView();
        handler.sendEmptyMessage(EnumAction.query.ordinal());
    }

    @Nullable
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // 使用自定义样式创建对话框
        return new Dialog(requireContext(), R.style.EditFragDialog) {
            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
            }
        };
    }



    public void initView() {
        title.setText(R.string.work_order_staff_add);

        Bitmap bitmapBack = BitmapUtil.scale(BitmapFactory.decodeResource(getResources(), R.drawable.back_media), 64, 64);
        configReturnButton(btnReturn, R.string.Return, bitmapBack);

        configActionButton(
                btnAction
                , R.string.save
                , BitmapUtil.scale(BitmapFactory.decodeResource(getResources(), R.drawable.save), 64, 64)
        );
        btnAction.setOnClickListener(this);
        btnReturn.setVisibility(View.INVISIBLE);
        btnAction.setVisibility(View.VISIBLE);
    }



    @Override
    public void onClick(View view) {
        if (view == null) return;
        if (view == btnReturn) {
            dismiss();
        } else if (view == btnAction) {
            handler.sendEmptyMessage(EnumAction.edit.ordinal());
        }
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
                    if (jsonMsg.isMsgType()) dismiss();
                    break;
                case query:
                    loadUsers();
                    break;
                case query_ui:
                    JsonMsg<List<UserRestEntity>> jsonMsgProduct = (JsonMsg<List<UserRestEntity>>) message.obj;
                    if (jsonMsgProduct == null) {
                        showToast("获取数据异常");
                        return;
                    }
                    list=jsonMsgProduct.getData();
                    showUsers();
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
        if (StringUtil.isEmpty(this.woId)) return;
        if (staffSelector.getSelectedItem()==null) return;
        UserRestEntity userRestEntity=(UserRestEntity)staffSelector.getSelectedItem();
        WorkOrderStaffRestEntity workOrderStaffRestEntity=new WorkOrderStaffRestEntity();
        workOrderStaffRestEntity.setWoId(woId);
        workOrderStaffRestEntity.setStaffId(userRestEntity.getUserCode());
        workOrderStaffRestEntity.setStaffName(userRestEntity.getUserName());
        workOrderStaffRestEntity.setWorkProgress(0);
        showProgressDialog("正在保存...");

        new Thread(()->{
            WorkOrderService productRestService=new WorkOrderService();
            JsonMsg<String> jsonMsg=productRestService.saveStaff(workOrderStaffRestEntity);
            handler.sendMessage(getMessage(EnumAction.edit_ui,jsonMsg));

            if (jsonMsg!=null && jsonMsg.isMsgType() && entityListener != null) {
                workOrderStaffRestEntity.setId(jsonMsg.getMsg());
                entityListener.onSaved(workOrderStaffRestEntity);

            }

        }).start();
    }

    private void loadUsers() {

        new Thread(() -> {
            SystemRestService systemRestService=new SystemRestService();
            JsonMsg<List<UserRestEntity>> jsonMsg=systemRestService.getAllUser();
            handler.sendMessage(getMessage(EnumAction.query_ui,jsonMsg));

        }).start();

    }

    private void showUsers() {
        ArrayAdapter<UserRestEntity> arrAdapterCate = new ArrayAdapter<>(context, R.layout.item_selected, this.list);
        staffSelector.setAdapter(arrAdapterCate);
        staffSelector.setPrompt("请选择：");
    }
    //#endregion

    //#region 外部事件
    public interface EntityListener {
        void onSaved(WorkOrderStaffRestEntity entity);

        void dismiss();
    }

    private EntityListener entityListener;

    public void setEntityListener(EntityListener entityListener) {
        this.entityListener = entityListener;
    }

    //#endregion
}
