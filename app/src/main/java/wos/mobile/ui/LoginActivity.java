package wos.mobile.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Locale;

import wos.mobile.ActivityEx;
import wos.mobile.R;
import wos.mobile.annotation.AnnotateUtil;
import wos.mobile.annotation.BindView;
import wos.mobile.entity.Constants;
import wos.mobile.entity.JsonMsg;
import wos.mobile.entity.Property;
import wos.mobile.entity.auth.UserSessionRestEntity;
import wos.mobile.service.AuthRestService;
import wos.mobile.util.BitmapUtil;
import wos.mobile.util.CommonFunc;
import wos.mobile.util.LogUtil;
import wos.mobile.util.StringUtil;
import wos.mobile.widget.CustomFab;

public class LoginActivity extends ActivityEx implements View.OnClickListener{
    //#region 变量
    @BindView(id = "txUserName")
    private EditText txUserName;

    @BindView(id = "txUserPassword")
    private EditText txUserPassword;

    @BindView(id = "btnLogin")
    private Button btnLogin;

    @BindView(id = "btnLanguage")
    private CustomFab btnLanguage;

    /**
     * 系统键监听
     */
    private DialogInterface.OnClickListener listener;

    //#endregion

    //#region 发送消息句柄
    @SuppressLint("HandlerLeak")
    private Handler handle = new Handler() {
        @Override
        public void handleMessage(Message message) {
            if (message==null) return;
            int what=message.what;
            switch (what) {
                case 0:
                    closeProgressDialog();
                    JsonMsg<UserSessionRestEntity> jsonMsg = (JsonMsg<UserSessionRestEntity>) message.obj;
                    if (jsonMsg.isMsgType()) {
                        UserSessionRestEntity userSessionRest = jsonMsg.getData();
                        Property.sessionId = userSessionRest.getSessionId();
                        Property.staffName = userSessionRest.getFullName();

                        showToast(getString(R.string.login_welcome) + userSessionRest.getFullName(), Toast.LENGTH_SHORT);

                        SharedPreferences sp = getSharedPreferences(
                                CommonFunc.CONFIG, 0);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString(CommonFunc.Config_UserId, userSessionRest.getUserName());
                        editor.putString(CommonFunc.Config_SessionId, Property.sessionId);
                        editor.putString(CommonFunc.Config_StaffName, Property.staffName);
                        editor.commit();
                        //此处必须用commit，不能使用apply。二者的主要区别在于是否阻塞式提交，commit确保保存数据，apply不确保一定保存成功
                        //editor.apply();

                        Intent intent = new Intent(context, WelcomeActivity.class);
                        startActivity(intent);
                        LoginActivity.this.finish();

                    } else {
                        showToast(jsonMsg.getMsg());
                    }
                    break;
            }
        }
    };

    //#endregion

    //#region android事件

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        Property.context = getApplicationContext();
        initView();


    }

    @Override
    public void onResume() {
        initListener();
        super.onResume();
        SharedPreferences sp = getSharedPreferences(CommonFunc.CONFIG, 0);
        String sessionId = sp.getString(CommonFunc.Config_SessionId, "");
//        BasicRestService.restServer = Constants.RestConfig.ServerInternal;

        if (StringUtil.isNotEmpty(sessionId)) {
            Property.sessionId = sessionId;
            showProgressDialog(R.string.logining);
            new Thread(() -> {
                login(sessionId);
            }).start();
        }
        hideKeyword(txUserName);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean exitApp = false;
        if (keyCode == KeyEvent.KEYCODE_BACK
                || keyCode == KeyEvent.KEYCODE_HOME) {
            AlertDialog isExit = new AlertDialog.Builder(this).create();
            isExit.setTitle(R.string.system_exit_title);
            isExit.setMessage(getString(R.string.system_exit_message));
            isExit.setButton(getString(R.string.yes), listener);
            isExit.setButton2(getString(R.string.no), listener);
            isExit.show();

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
        //#region 界面
        WindowManager windowManager = getWindowManager();
        android.view.Display display = windowManager.getDefaultDisplay();
        Property.screenheight = display.getHeight();
        Property.screenwidth = display.getWidth();

        AnnotateUtil.initBindView(this);

        SharedPreferences sp = getSharedPreferences(
                CommonFunc.CONFIG, 0);

        //#endregion

        txUserName.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT
                    || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {//让mPasswordEdit获取输入焦点
                txUserPassword.requestFocus();
                return true;
            }
            return false;
        });

        txUserPassword.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT
                    || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {//让mPasswordEdit获取输入焦点
                return btnLogin.callOnClick();
            }
            return false;
        });


        Bitmap bitmap = BitmapUtil.scale(BitmapFactory.decodeResource(getResources(), R.drawable.login), 64, 64);
        configReturnButton(btnLogin, R.string.login, bitmap);

        setOnClickListener(Arrays.asList(btnLogin, btnLanguage), this);
    }


    @Override
    public void onClick(View view) {
        if (view == btnLogin) {
            //#region 登陆广播
            Intent intent = new Intent();
            intent.setAction("login_message");
            intent.setPackage(Property.PACKAGE_NAME);
            sendBroadcast(intent);
            //#endregion

            //#region 用户名不能为空
            String userName = txUserName.getText().toString();
            if (StringUtil.isEmpty(userName)) {
                showToast(R.string.exp_usernameempty);
                return;
            }
            //#endregion

            //#region 密码不能为空
            String userPassword = txUserPassword.getText().toString();
            if (StringUtil.isEmpty(userPassword)) {
                showToast(R.string.exp_passwrodempty);
                return;
            }
            //#endregion

            //#region 登陆
            showProgressDialog(R.string.logining);

            new Thread(() -> {
                login(userName, userPassword);
            }).start();

            //#endregion
        } else if (view == btnLanguage) {
            showPopupLanguage(view);
        }
    }
    //#endregion

    //#region 动作

    /**
     * 登录
     */
    private void login(String userName, String userPassword) {
        AuthRestService authRestService = new AuthRestService();
        Message message = new Message();
        message.what = 0;
        message.obj = authRestService.login(userName, userPassword);
        handle.sendMessage(message);
    }

    /**
     * 通过令牌登录
     *
     * @param sessionId 令牌标识
     */
    private void login(String sessionId) {
        AuthRestService authRestService = new AuthRestService();
        Message message = new Message();
        message.what = 0;
        message.obj = authRestService.login(sessionId);
        handle.sendMessage(message);
    }

    /**
     * 初始化监听
     */
    private void initListener() {
        listener = (DialogInterface dialog, int which) -> {
            switch (which) {
                case AlertDialog.BUTTON_POSITIVE:
                    System.exit(0);
                    break;
                case AlertDialog.BUTTON_NEGATIVE:
                    break;
                default:
                    break;
            }
        };
    }

    /**
     * 显示界面语言快捷菜单
     *
     * @param view 控件
     */
    private void showPopupLanguage(View view) {
        PopupMenu popupMenu = getPopMenu(this, view, R.menu.menu_empty);

        popupMenu.getMenu().add(0, 1001, 0, "English").setIcon(R.drawable.english);
        popupMenu.getMenu().add(0, 1002, 0, "简体中文").setIcon(R.drawable.chinese);

        popupMenu.setOnMenuItemClickListener(item -> {
            int menuItemId = item.getItemId();
            String selectedLanguage = "zh";
            switch (menuItemId) {
                case 1001:
                    selectedLanguage = "en";
                    break;
                case 1002:
                    selectedLanguage = "zh";
                    break;
            }
            setAppLocale(selectedLanguage);
            return false;
        });

        popupMenu.show();
    }

    /**
     * 设定界面语言
     *
     * @param localeCode 界面语言编码
     */
    private void setAppLocale(String localeCode) {
        Locale locale = new Locale(localeCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        recreate();
    }

    //#endregion
}
