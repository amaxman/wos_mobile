package wos.mobile.ui.workOrder;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;

import androidx.activity.result.contract.ActivityResultContracts;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import wos.mobile.R;
import wos.mobile.annotation.AnnotateUtil;
import wos.mobile.ActivityEx;
import wos.mobile.entity.Constants;
import wos.mobile.entity.EnumAction;
import wos.mobile.entity.JsonMsg;
import wos.mobile.entity.Page;
import wos.mobile.entity.workOrder.WorkOrderRestEntity;
import wos.mobile.service.WorkOrderService;
import wos.mobile.util.BitmapUtil;
import wos.mobile.util.StringUtil;

public class WorkOrderActivity extends ActivityEx implements View.OnClickListener {
    //#region 变量
    private final WorkOrderService service = new WorkOrderService();

    private final List<WorkOrderRestEntity> list = new ArrayList<>();

    private WorkOrderAdapter adapter;

    //#endregion

    //#region handle
    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message message) {
            EnumAction action = EnumAction.getByOrdinal(message.what);
            switch (action) {
                case query:
                    pageNo=1;
                    queryData();
                    break;
                case query_ui:
                    closeProgressDialog();
                    JsonMsg<Page<WorkOrderRestEntity>> clientRestPageJson = (JsonMsg<Page<WorkOrderRestEntity>>) message.obj;
                    showToast(clientRestPageJson.getMsg());
                    if (!clientRestPageJson.isMsgType()) {
                        pageNo=1;
                        reloadBtnPage();
                        return;
                    }
                    list.clear();
                    Page<WorkOrderRestEntity> page=clientRestPageJson.getData();
                    list.addAll(page.getList());
                    adapter.notifyDataSetChanged();
                    pageNo=page.getPageNo();
                    pageSize=page.getPageSize();
                    count=page.getCount();
                    reloadBtnPage();
                    break;
                case edit_activity:
                    Intent intentEdit = new Intent(context, WorkOrderEditActivity.class);
                    intentEdit.putExtra("data", JSONObject.toJSONString(message.obj));
                    activityResultLauncher.launch(intentEdit);
                    break;
                case delete:
                    delete((WorkOrderRestEntity) message.obj);
                    break;
                case delete_ui:
                    closeProgressDialog();
                    showToast("删除成功");
                    String deleteId=String.valueOf(message.obj);
                    WorkOrderRestEntity workOrderDeleted=list.stream().filter(p->StringUtil.equalsAnyIgnoreCase(p.getId(),deleteId)).findFirst().orElse(null);
                    if (workOrderDeleted!=null) {
                        list.remove(workOrderDeleted);
                    }
                    adapter.notifyDataSetChanged();
                    reloadBtnPage();
                    break;
                case detail_activity:
                    Intent intentPathPoint=new Intent(context,WorkOrderStaffActivity.class);
                    intentPathPoint.putExtra("woId",String.valueOf(message.obj));
                    startActivity(intentPathPoint);
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

        btnAction.setVisibility(View.VISIBLE);
        configActionButton(
                btnAction
                , R.string.add
                , BitmapUtil.scale(BitmapFactory.decodeResource(getResources(), R.drawable.add), 64, 64)
        );

        txKeyword.clearFocus();

        setOnClickListener(Arrays.asList(btnAction, btnReturn, btnQuery,btnNext,btnPre,btnFab), this);


        btnNext.setVisibility(View.GONE);
        btnPre.setVisibility(View.GONE);
        btnFab.setVisibility(View.GONE);

        if (labVersion != null) labVersion.setText(getVersionName());

        // 注册 ActivityResultLauncher
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                (result) -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
//                            PathRestEntity ruleRestEntity= JSONObject.parseObject(data.getStringExtra("entity"),PathRestEntity.class);
//                            if (ruleRestEntity==null) return;
//                            String id= ruleRestEntity.getId();
//                            if (StringUtil.isEmpty(id)) return;
//                            PathRestEntity entity=list.stream().filter(p->StringUtil.equalsAnyIgnoreCase(p.getId(),id)).findFirst().orElse(null);
//                            if (entity==null) {
//                                list.add(ruleRestEntity);
//                            } else {
//                                entity.setGroupName(ruleRestEntity.getGroupName());
//                                entity.setGroupCode(ruleRestEntity.getGroupCode());
//                            }
//                            adapter.notifyDataSetChanged();
                        }
                    }
                }
        );
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
            activityResultLauncher.launch(new Intent(context, WorkOrderEditActivity.class));
        } else if (view == btnNext) {
            pageNo--;
            queryData();
        } else if (view==btnPre) {
            pageNo++;
            queryData();
        }
    }
    //#endregion

    //#region 数据
    private void bindData() {
        loadContentSize();
        adapter = new WorkOrderAdapter(context, list, contentWidth, contentHeight, handler, true);
        listV.setAdapter(adapter);

        listV.setOnItemRemoveListener((position) -> {
            WorkOrderRestEntity entity = list.get(position);
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
            WorkOrderRestEntity entity = list.get(i);
            if (entity == null) {
                handler.sendMessage(getMessage(EnumAction.toast, getString(R.string.exp_no_exist)));
                return;
            }
            handler.sendMessage(getMessage(EnumAction.detail_activity, entity.getId()));

        });
    }

    public void queryData() {
        showProgressDialog(R.string.loading_data);
        hideKeyword(txKeyword);
        new Thread(() -> {
            String keyword = txKeyword.getText().toString().trim();
            JsonMsg<Page<WorkOrderRestEntity>> jsonMsg = service.find(keyword, pageNo, pageSize);
            handler.sendMessage(getMessage(EnumAction.query_ui, jsonMsg));
        }).start();
    }

    private void reloadBtnPage() {
        if (count==0) {
            btnPre.setVisibility(View.INVISIBLE);
            btnNext.setVisibility(View.INVISIBLE);
        } else {
            if (pageNo==1) {
                btnPre.setVisibility(View.INVISIBLE);
            } else {
                btnPre.setVisibility(View.VISIBLE);
            }

            if (pageNo*pageSize>=count) {
                btnNext.setVisibility(View.INVISIBLE);
            } else {
                btnNext.setVisibility(View.VISIBLE);
            }
        }
    }
    //#endregion

    //#region 函数
    private void delete(WorkOrderRestEntity entity) {
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
                                JsonMsg<Void> json=service.delete(entity.getId());
                                if (json==null) {
                                    message.obj=getString(R.string.except_server_return_nothing);
                                    handler.sendMessage(message);
                                    return;
                                }
                                if (!json.isMsgType()) {
                                    message.obj=json.getMsg();
                                    handler.sendMessage(message);
                                    return;
                                }
                                message.what=EnumAction.delete_ui.ordinal();
                                message.obj=entity.getId();
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
        WorkOrderRestEntity entity = list.stream().filter(p -> StringUtil.equalsAnyIgnoreCase(p.getId(), id)).findFirst().orElse(null);
        if (entity != null) {
            list.remove(entity);
            adapter.notifyDataSetChanged();
        }
    }
    //#endregion

}
