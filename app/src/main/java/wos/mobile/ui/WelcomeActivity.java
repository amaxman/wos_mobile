package wos.mobile.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.GridView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

import wos.mobile.ActivityEx;
import wos.mobile.R;
import wos.mobile.annotation.AnnotateUtil;
import wos.mobile.annotation.BindView;
import wos.mobile.entity.EnumAction;
import wos.mobile.entity.JsonMsg;
import wos.mobile.entity.Property;
import wos.mobile.entity.auth.PermissionRestEntity;
import wos.mobile.service.AuthRestService;
import wos.mobile.ui.workOrder.WorkOrderActivity;
import wos.mobile.ui.workOrder.WorkOrderStaffMyActivity;
import wos.mobile.util.BitmapUtil;
import wos.mobile.util.CommonFunc;
import wos.mobile.util.LogUtil;

public class WelcomeActivity extends ActivityEx implements View.OnClickListener {

    //#region 变量

    @BindView(id = "gvFunction")
    private GridView gvFunction;


    private WelcomeAdapter welcomeAdapter = null;

    private final List<PermissionRestEntity> permissionList = new ArrayList<>();

    /**
     * 系统键监听
     */
    private DialogInterface.OnClickListener listenerExit, listenerLogout;

    String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.CAMERA",
            "android.permission.RECORD_AUDIO",
            "android.permission.REQUEST_INSTALL_PACKAGES",
    };

    //#endregion

    //region handler
    private final Handler handler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(@NonNull Message message) {
            String funcCode = String.valueOf(message.obj);
            EnumAction what = EnumAction.getByOrdinal(message.what);

            switch (what) {
                case load_permission:
                    load_permission();
                    break;
                case load_permission_ui:
                    load_permission_ui(message);
                    break;
                case acton:
                    doAction(funcCode);
                    break;
                case launch:
                    launchActivity(funcCode);
                    break;
                case logout:
                   logout2Server();
                    break;
                case logout_ui:
                    logout();
                    break;
                case toast:
                    closeProgressDialog();
                    showToast(String.valueOf(message.obj));
                    break;
                default:
                    showToast(getString(R.string.welcome_undefined_func_code), Toast.LENGTH_SHORT);
                    break;

            }
        }
    };
    //#endregion

    //#region 系统事件
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
        AnnotateUtil.initBindView(this);
        initView();
        checkPermission();
    }

    @Override
    public void onResume() {
        initListener();
        super.onResume();

        handler.sendEmptyMessage(EnumAction.load_permission.ordinal());

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean exitApp = false;
        if (keyCode == KeyEvent.KEYCODE_BACK
                || keyCode == KeyEvent.KEYCODE_HOME) {
            showExitDlg();
        } else {
            exitApp = super.onKeyDown(keyCode, event);
        }
        return exitApp;
    }

    /**
     * 初始化布局
     */
    @Override
    public void initView() {

        title.setText(R.string.app_name);

        Bitmap bitmap = BitmapUtil.scale(BitmapFactory.decodeResource(getResources(), R.drawable.logout), 64, 64);
        configReturnButton(btnReturn, R.string.access_logout, bitmap);
        btnReturn.setOnClickListener(this);

        Bitmap bitmapAction = BitmapUtil.scale(BitmapFactory.decodeResource(getResources(), R.drawable.settings), 64, 64);
        configActionButton(btnAction, R.string.setting, bitmapAction);
        btnAction.setOnClickListener(this);


        loadContentSize();

        welcomeAdapter = new WelcomeAdapter(this,
                permissionList, handler, contentWidth, contentHeight);
        gvFunction.setAdapter(welcomeAdapter);


    }

    /**
     * 加载权限
     */
    private void loadPermission() {
        AuthRestService authRestService = new AuthRestService();
        Message message=new Message();
        try {
            message.obj=authRestService.getAccessFunc(Property.sessionId);
            message.what=EnumAction.load_permission_ui.ordinal();

        } catch (Exception ex) {
            message.obj=ex.getMessage();
            message.what=EnumAction.toast.ordinal();
        }
        handler.sendMessage(message);

    }

    /**
     * 显示权限
     */
    private void showPermission(JsonMsg<List<PermissionRestEntity>> json) {

        permissionList.clear();
        try {
            if (json == null) return;
            if (!json.isMsgType()) {
                handler.sendMessage(getMessage(EnumAction.toast, json.getMsg()));
                return;
            }
            List<PermissionRestEntity> funcCodeList = json.getData();
            funcCodeList.forEach(item -> {
                permissionList.add(item);
            });
        } catch (Exception ex) {
            handler.sendMessage(getMessage(EnumAction.toast, ex.getMessage()));
        }


        welcomeAdapter.notifyDataSetChanged();
    }

    private void showExitDlg() {
        AlertDialog dlgExitSystem = new AlertDialog.Builder(this).create();
        dlgExitSystem.setTitle(R.string.system_exit_title);
        dlgExitSystem.setMessage(getString(R.string.system_exit_message));
        dlgExitSystem.setButton(getString(R.string.yes), listenerExit);
        dlgExitSystem.setButton2(getString(R.string.no), listenerExit);
        dlgExitSystem.show();
    }

    private void showLogOutDlg() {
        AlertDialog dlgLogout = new AlertDialog.Builder(this).create();
        dlgLogout.setTitle(R.string.system_logout_title);
        dlgLogout.setMessage(getString(R.string.system_logout_message));
        dlgLogout.setButton(getString(R.string.yes), listenerLogout);
        dlgLogout.setButton2(getString(R.string.no), listenerLogout);
        dlgLogout.show();
    }

    private void initListener() {
        listenerExit = (dialog, which) -> {
            switch (which) {
                case AlertDialog.BUTTON_POSITIVE:
                    exitSystem();
                    break;
                case AlertDialog.BUTTON_NEGATIVE:
                    LogUtil.i("Cancel to exit system");
                    break;
                default:
                    break;
            }
        };

        listenerLogout = (dialog, which) -> {
            switch (which) {
                case AlertDialog.BUTTON_POSITIVE:
                    handler.sendEmptyMessage(EnumAction.logout.ordinal());
                    break;
                case AlertDialog.BUTTON_NEGATIVE:
                    LogUtil.i("Cancel to logout");
                    break;
                default:
                    break;
            }
        };
    }



    /**
     * 检查系统权限
     */
    private void checkPermission() {
        int requestCode = 1;
        //权限是否已经赋予
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //未赋予权限，申请权限
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission
                    .WRITE_EXTERNAL_STORAGE)) {
                //选择不开启权限的时候，提示用户
                handler.sendMessage(getMessage(EnumAction.toast, getString(R.string.system_require_permission)));
            }
            //申请权限
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, requestCode);
        }
    }

    @Override
    public void onClick(View view) {
        if (view == btnReturn) {
            showLogOutDlg();
        } else if (view == btnAction) {
            showPopupMenu(view);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //#endregion

    //#region action

    /**
     * 右上角action动作
     *
     * @param view 视图
     */
    private void showPopupMenu(View view) {
        PopupMenu popupMenu = getPopMenu(this, view, R.menu.welcome_action);

        popupMenu.setOnMenuItemClickListener(item -> {
            int menuItemId = item.getItemId();
            if (menuItemId == R.id.menuRefresh) {
                showProgressDialog(R.string.access_loading);
                handler.sendEmptyMessage(EnumAction.load_permission.ordinal());
                return true;
            } else if (menuItemId == R.id.menuChangePassword) {
                launchActivity("passwordChange");
                return true;
            }
            return false;
        });

        popupMenu.show();
    }
    //#endregion

    //#region 授权
    private void load_permission() {
        showProgressDialog(R.string.access_loading);
        new Thread(() -> {
            loadPermission();
        }).start();
    }

    private void load_permission_ui(Message message) {
        JsonMsg<List<PermissionRestEntity>> jsonMsgAccess = (JsonMsg<List<PermissionRestEntity>>) message.obj;
        showPermission(jsonMsgAccess);
        closeProgressDialog();
    }

    /**
     * 自服务器注销登陆
     */
    private void logout2Server() {
        new Thread(()->{
            AuthRestService authRestService=new AuthRestService();
            Message message=new Message();
            try {
                message.obj=authRestService.logout(Property.sessionId);
                message.what= EnumAction.logout_ui.ordinal();
            } catch (Exception ex) {
                message.obj=ex.getMessage();
                message.what=EnumAction.toast.ordinal();
            }
            handler.sendMessage(message);


        }).start();
    }

    private void logout() {

        SharedPreferences sp = getSharedPreferences(
                CommonFunc.CONFIG, 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(CommonFunc.Config_SessionId);
        editor.remove(CommonFunc.Config_StaffName);
        editor.apply();

        Property.sessionId = "";
        Property.staffName = "";

        Intent intent = new Intent(context, LoginActivity.class);
        startActivity(intent);
        this.finish();
    }

    private void doAction(String funcCode) {
        switch (funcCode) {
            case "reload":
                handler.sendEmptyMessage(EnumAction.load_permission.ordinal());
                break;
        }
    }

    /**
     * 启动窗体
     *
     * @param funcCode 功能码
     */
    private void launchActivity(String funcCode) {
        switch (funcCode) {
            case "work_order":
                startActivity(new Intent(context, WorkOrderActivity.class));
                break;
            case "my_work_order":
                startActivity(new Intent(context, WorkOrderStaffMyActivity.class));
                break;
            case "config_patrol_point": //点位配置
            case "mobile_user": //移动用户
            case "setting": //系统设定
            case "emergency":   //emergency
                break;
            default:
                handler.sendMessage(getMessage(EnumAction.toast, getString(R.string.welcome_operate_undefined)));
                break;
        }
    }
    //#endregion
}
