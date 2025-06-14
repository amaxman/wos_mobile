package wos.mobile.ui.workOrder;

import android.app.Dialog;
import android.content.Context;
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
import android.widget.EditText;
import android.widget.SeekBar;

import androidx.annotation.Nullable;

import wos.mobile.R;
import wos.mobile.annotation.AnnotateUtil;
import wos.mobile.annotation.BindView;
import wos.mobile.entity.EnumAction;
import wos.mobile.entity.JsonMsg;
import wos.mobile.service.WorkOrderService;
import wos.mobile.util.BitmapUtil;
import wos.mobile.util.StringUtil;
import wos.mobile.widget.BasicDialogFragment;

public class WorkOrderStaffProgressFragment extends BasicDialogFragment implements View.OnClickListener {

    //#region 变量
    private String id;
    private Integer progress;

    //#endregion

    //#region 控件


    @BindView(id = "sbProgress")
    private SeekBar sbProgress;

    @BindView(id = "txProgress")
    private EditText txProgress;


    //#endregion

    //#region 事件

    public WorkOrderStaffProgressFragment(String id, int progress, Context context) {
        super(context);
        this.id = id;
        this.progress=progress;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=getCreateView(inflater,container,savedInstanceState,R.layout.activity_work_order_staff_progress);
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

    @Override
    public void onStart() {
        super.onStart();

        // 获取对话框窗口
        Dialog dialog = getDialog();
        if (dialog != null) {
            // 获取窗口属性
            Window window = dialog.getWindow();
            if (window != null) {
                // 设置窗口宽度和高度
                WindowManager.LayoutParams params = window.getAttributes();
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
//                params.height = 500; // 以像素为单位设置固定高度
                // 或者使用百分比
                params.height = (int) (getResources().getDisplayMetrics().heightPixels * 0.4); // 80% 屏幕高度
                window.setAttributes(params);
            }
        }
    }



    public void initView() {
        title.setText(R.string.work_order_staff_modify_progress);

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


        sbProgress.setProgress(progress);
        txProgress.setText(String.valueOf(progress));

        sbProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txProgress.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // 开始拖动时的回调
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                progress=seekBar.getProgress();

            }
        });



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
                    JsonMsg<Void> jsonMsg = (JsonMsg<Void>) message.obj;
                    showToast(jsonMsg.getMsg());
                    if (jsonMsg.isMsgType()) dismiss();
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
        if (StringUtil.isEmpty(this.id)) return;
        showProgressDialog(R.string.saving);

        new Thread(()->{
            WorkOrderService productRestService=new WorkOrderService();
            JsonMsg<Void> jsonMsg=productRestService.staffSave(id,progress);
            handler.sendMessage(getMessage(EnumAction.edit_ui,jsonMsg));

            if (jsonMsg!=null && jsonMsg.isMsgType() && progressListener != null) {
                progressListener.onSaved(id,progress);

            }

        }).start();
    }
    //#endregion

    //#region 外部事件
    public interface ProgressListener {
        void onSaved(String id,int progress);

        void dismiss();
    }

    private ProgressListener progressListener;

    public void setProgressListener(ProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    //#endregion
}
