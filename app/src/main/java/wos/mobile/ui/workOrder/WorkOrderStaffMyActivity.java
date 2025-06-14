package wos.mobile.ui.workOrder;

import android.app.AlertDialog;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import wos.mobile.ActivityEx;
import wos.mobile.R;
import wos.mobile.annotation.AnnotateUtil;
import wos.mobile.entity.EnumAction;
import wos.mobile.entity.JsonMsg;
import wos.mobile.entity.Page;
import wos.mobile.entity.workOrder.WorkOrderStaffRestEntity;
import wos.mobile.service.WorkOrderService;
import wos.mobile.util.BitmapUtil;
import wos.mobile.util.StringUtil;

public class WorkOrderStaffMyActivity extends ActivityEx implements View.OnClickListener,WorkOrderStaffProgressFragment.ProgressListener {
    //#region 变量
    private final WorkOrderService service = new WorkOrderService();

    private final List<WorkOrderStaffRestEntity> list = new ArrayList<>();

    private WorkOrderStaffMyAdapter adapter;

    //#endregion

    //#region handle
    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message message) {
            EnumAction action = EnumAction.getByOrdinal(message.what);
            switch (action) {
                case query:
                    queryData();
                    break;
                case query_ui:
                    closeProgressDialog();
                    JsonMsg<Page<WorkOrderStaffRestEntity>> clientRestPageJson = (JsonMsg<Page<WorkOrderStaffRestEntity>>) message.obj;
                    showToast(clientRestPageJson.getMsg());
                    if (!clientRestPageJson.isMsgType()) {
                        return;
                    }
                    list.clear();
                    list.addAll(clientRestPageJson.getData().getList());
                    count=clientRestPageJson.getData().getCount();
                    pageNo=clientRestPageJson.getData().getPageNo();
                    reloadBtnPage();
                    adapter.notifyDataSetChanged();
                    break;
                case reload:
                    adapter.updateData(list);
                    closeProgressDialog();
                    break;
                case close:
                    break;
                case edit:
                    complete(String.valueOf(message.obj));
                case edit_ui:
                    closeProgressDialog();
                    String completedId=String.valueOf(message.obj);
                    WorkOrderStaffRestEntity entityCompleted=list.stream()
                            .filter(p->StringUtil.equalsAnyIgnoreCase(p.getId(),completedId))
                            .findFirst().orElse(null);
                    if (entityCompleted!=null) {
                        entityCompleted.setWorkProgress(100);
                    }
                    adapter.updateData(list);
                    closeProgressDialog();
                    break;
                case edit_activity:
                    WorkOrderStaffRestEntity workOrderStaffRestEntityEdit=(WorkOrderStaffRestEntity)message.obj;
                    WorkOrderStaffProgressFragment workOrderStaffProgressFragment=new WorkOrderStaffProgressFragment(
                            workOrderStaffRestEntityEdit.getId(),
                            workOrderStaffRestEntityEdit.getWorkProgress(),WorkOrderStaffMyActivity.this);
                    workOrderStaffProgressFragment.setProgressListener(WorkOrderStaffMyActivity.this);
                    workOrderStaffProgressFragment.show(getSupportFragmentManager(),WorkOrderStaffProgressFragment.class.getSimpleName());
                    break;
                case detail_activity:
//                    Intent intentPathPoint=new Intent(context,PathPointActivity.class);
//                    intentPathPoint.putExtra("groupId",String.valueOf(message.obj));
//                    startActivity(intentPathPoint);
                    break;
                case toast:
                    closeProgressDialog();
                    showToast(String.valueOf(message.obj));
                    break;
            }
        }
    };
    //#endregion

    //#region 系统生命周期
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        this.context = this;
        AnnotateUtil.initBindView(this);
        initView();
        bindData();
        handler.sendEmptyMessage(EnumAction.query.ordinal());

    }

    @Override
    public void initView() {
        title.setText(R.string.work_order);
        Bitmap bitmapBack = BitmapUtil.scale(BitmapFactory.decodeResource(getResources(), R.drawable.back_media), 64, 64);
        configReturnButton(btnReturn, R.string.Return, bitmapBack);

        btnAction.setVisibility(View.INVISIBLE);

        txKeyword.clearFocus();

        setOnClickListener(Arrays.asList(btnAction, btnReturn, btnQuery, btnNext, btnPre, btnFab), this);


        btnNext.setVisibility(View.GONE);
        btnPre.setVisibility(View.GONE);
        btnFab.setVisibility(View.GONE);

        if (labVersion != null) labVersion.setText(getVersionName());
    }
    //#endregion

    //#region 事件
    @Override
    public void onClick(View view) {
        if (view == null) return;
        if (view == btnReturn) {
            finish();
        } else if (view == btnQuery) {
            pageNo=1;
            queryData();
        } else if (view == btnAction) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
//            WorkOrderStaffAddFragment workOrderStaffAddFragment=new WorkOrderStaffAddFragment(this.woId,this);
//            workOrderStaffAddFragment.setEntityListener(this);
//            workOrderStaffAddFragment.show(getSupportFragmentManager(),WorkOrderStaffAddFragment.class.getSimpleName());
        } else if (view==btnPre) {
            pageNo--;
            queryData();
        }  else if (view==btnNext) {
            pageNo++;
            queryData();
        }
    }
    //#endregion

    //#region 数据
    private void bindData() {
        loadContentSize();
        adapter = new WorkOrderStaffMyAdapter(context, list, contentWidth, contentHeight, handler);
        listV.setAdapter(adapter);

        listV.setOnItemRemoveListener((position) -> {
            WorkOrderStaffRestEntity entity = list.get(position);
            if (entity == null) {
                handler.sendMessage(getMessage(EnumAction.toast, getString(R.string.exp_no_exist)));
                return;
            }
            handler.sendMessage(getMessage(EnumAction.delete, entity));
        });

        listV.setOnItemClickListener((adapterView, view, i, l) -> {
            if (listV.mDelta != 0) {
                listV.clear();
                return;
            }
            WorkOrderStaffRestEntity entity = list.get(i);
            if (entity == null) {
                handler.sendMessage(getMessage(EnumAction.toast, getString(R.string.exp_no_exist)));
                return;
            }
//            handler.sendMessage(getMessage(EnumAction.edit_activity, entity.getId()));

        });

        listV.setOnItemLongClickListener(((adapterView, view, i, l) -> {
            WorkOrderStaffRestEntity entity = list.get(i);
            if (entity == null) {
                handler.sendMessage(getMessage(EnumAction.toast, getString(R.string.exp_no_exist)));
                return true;
            }
//            handler.sendMessage(getMessage(EnumAction.detail_activity, entity.getId()));
            return true;
        }));
    }

    public void queryData() {
        showProgressDialog(R.string.loading_data);
        hideKeyword(txKeyword);
        String keyword=txKeyword.getText().toString();
        new Thread(() -> {

            JsonMsg<Page<WorkOrderStaffRestEntity>> jsonMsg = service.staffPage(keyword,"","",null,null,null,pageNo,pageSize);
            handler.sendMessage(getMessage(EnumAction.query_ui, jsonMsg));
        }).start();
    }

    private void reloadBtnPage() {
        if (count == 0) {
            btnPre.setVisibility(View.INVISIBLE);
            btnNext.setVisibility(View.INVISIBLE);
        } else {
            if (pageNo == 1) {
                btnPre.setVisibility(View.INVISIBLE);
            } else {
                btnPre.setVisibility(View.VISIBLE);
            }

            if (pageNo * pageSize >= count) {
                btnNext.setVisibility(View.INVISIBLE);
            } else {
                btnNext.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 完成工单
     * @param id
     */
    private void complete(String id) {
        Message message = new Message();
        message.what = EnumAction.toast.ordinal();
        if (StringUtil.isEmpty(id)) {
            message.obj = getString(R.string.exp_no_exist);
            handler.sendMessage(message);
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle(R.string.Prompt)
                .setMessage(R.string.work_order_staff_modify_complete_confirm)
                .setPositiveButton(
                        R.string.confirm,
                        (dialog, witch) -> {
                            showProgressDialog(R.string.saving);
                            new Thread(()->{
                                JsonMsg<Void> json=service.staffSave(id,100);
                                handler.sendMessage(getMessage(EnumAction.toast, json.getMsg()));
                                if (!json.isMsgType()) return;
                                JsonMsg<String> jsonCompleted=JsonMsg.success(id,json.getMsg());
                                handler.sendMessage(getMessage(EnumAction.edit_ui, jsonCompleted));
                            }).start();

                        }
                ).setNegativeButton(R.string.Cancel, null)
                .show();
    }
    //#endregion

    //#region 函数


    @Override
    public void onSaved(String id,int progress) {
        if (StringUtil.isEmpty(id)) return;
        WorkOrderStaffRestEntity cacheEntity=list.stream().filter(p->StringUtil.equalsAnyIgnoreCase(p.getId(),id)).findFirst().orElse(null);
        if (cacheEntity!=null) {
            cacheEntity.setWorkProgress(progress);
            handler.sendEmptyMessage(EnumAction.reload.ordinal());
        }
    }

    @Override
    public void dismiss() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
    }
    //#endregion

}
