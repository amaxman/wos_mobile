package wos.mobile.ui.workOrder;

import android.app.AlertDialog;
import android.content.Intent;
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
import wos.mobile.entity.workOrder.WorkOrderStaffRestEntity;
import wos.mobile.service.WorkOrderService;
import wos.mobile.util.BitmapUtil;
import wos.mobile.util.StringUtil;

public class WorkOrderStaffActivity extends ActivityEx implements View.OnClickListener,WorkOrderStaffAddFragment.EntityListener {
    //#region 变量
    private final WorkOrderService service = new WorkOrderService();

    private final List<WorkOrderStaffRestEntity> list = new ArrayList<>();

    private WorkOrderStaffAdapter adapter;

    private String woId;

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
                    JsonMsg<List<WorkOrderStaffRestEntity>> clientRestPageJson = (JsonMsg<List<WorkOrderStaffRestEntity>>) message.obj;
                    showToast(clientRestPageJson.getMsg());
                    if (!clientRestPageJson.isMsgType()) {
                        return;
                    }
                    list.clear();
                    list.addAll(clientRestPageJson.getData());
                    adapter.notifyDataSetChanged();
                    break;
                case reload:
                    adapter.updateData(list);
                    closeProgressDialog();
                    break;
                case edit_activity:
//                    Intent intentEdit = new Intent(context, WorkOrderEditActivity.class);
//                    intentEdit.putExtra("data", JSONObject.toJSONString(message.obj));
//                    activityResultLauncher.launch(intentEdit);
                    break;
                case delete:
                    delete((WorkOrderStaffRestEntity) message.obj);
                    break;
                case delete_ui:
                    closeProgressDialog();
                    showToast("删除成功");
                    String deleteId = String.valueOf(message.obj);
                    deleteSuccess(deleteId);
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
        Intent intent = getIntent();
        this.woId = intent.getStringExtra("woId");
        handler.sendEmptyMessage(EnumAction.query.ordinal());

    }

    @Override
    public void initView() {
        title.setText(R.string.work_order);
        Bitmap bitmapBack = BitmapUtil.scale(BitmapFactory.decodeResource(getResources(), R.drawable.back_media), 64, 64);
        configReturnButton(btnReturn, R.string.Return, bitmapBack);

        btnAction.setVisibility(View.VISIBLE);
        configActionButton(
                btnAction
                , R.string.add
                , BitmapUtil.scale(BitmapFactory.decodeResource(getResources(), R.drawable.add), 64, 64)
        );

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
            queryData();
        } else if (view == btnAction) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
            WorkOrderStaffAddFragment workOrderStaffAddFragment=new WorkOrderStaffAddFragment(this.woId,this);
            workOrderStaffAddFragment.setEntityListener(this);
            workOrderStaffAddFragment.show(getSupportFragmentManager(),WorkOrderStaffAddFragment.class.getSimpleName());
        }
    }
    //#endregion

    //#region 数据
    private void bindData() {
        loadContentSize();
        adapter = new WorkOrderStaffAdapter(context, list, contentWidth, contentHeight, handler, false);
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
        new Thread(() -> {

            JsonMsg<List<WorkOrderStaffRestEntity>> jsonMsg = service.staffList(this.woId);
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
    //#endregion

    //#region 函数
    private void delete(WorkOrderStaffRestEntity entity) {
        Message message = new Message();
        message.what = EnumAction.toast.ordinal();
        if (entity == null) {
            message.obj = getString(R.string.exp_no_exist);
            handler.sendMessage(message);
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle(R.string.Prompt)
                .setMessage(R.string.confirmDelete)
                .setPositiveButton(
                        R.string.delete,
                        (dialog, witch) -> {
                            showProgressDialog(R.string.deleting);
                            new Thread(() -> {
                                JsonMsg<Void> json = service.deleteStaff(entity.getId());
                                if (json == null) {
                                    message.obj = getString(R.string.except_server_return_nothing);
                                    handler.sendMessage(message);
                                    return;
                                }
                                if (!json.isMsgType()) {
                                    message.obj = json.getMsg();
                                    handler.sendMessage(message);
                                    return;
                                }
                                message.what = EnumAction.delete_ui.ordinal();
                                message.obj = entity.getId();
                                handler.sendMessage(message);
                            }).start();

                        }
                ).setNegativeButton(R.string.Cancel, null)
                .show();
    }

    private void deleteSuccess(String id) {
        closeProgressDialog();
        Message message = new Message();
        message.what = EnumAction.toast.ordinal();
        if (StringUtil.isEmpty(id)) {
            message.obj = getString(R.string.exp_id_null);
            handler.sendMessage(message);
            return;
        }
        WorkOrderStaffRestEntity entity = list.stream().filter(p -> StringUtil.equalsAnyIgnoreCase(p.getId(), id)).findFirst().orElse(null);
        if (entity != null) {
            list.remove(entity);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onSaved(WorkOrderStaffRestEntity entity) {
        String id=entity.getId();
        if (StringUtil.isEmpty(id)) return;
        WorkOrderStaffRestEntity cacheEntity=list.stream().filter(p->StringUtil.equalsAnyIgnoreCase(p.getId(),id)).findFirst().orElse(null);
        if (cacheEntity==null) {
            list.add(entity);
            handler.sendEmptyMessage(EnumAction.reload.ordinal());
        } else {
            handler.sendEmptyMessage(EnumAction.query.ordinal());
        }
    }

    @Override
    public void dismiss() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
    }
    //#endregion

}
