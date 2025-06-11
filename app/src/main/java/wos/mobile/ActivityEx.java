package wos.mobile;

import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.fragment.app.FragmentActivity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import wos.mobile.annotation.BindView;
import wos.mobile.entity.Constants;
import wos.mobile.entity.EnumAction;
import wos.mobile.entity.Property;
import wos.mobile.util.CommonFunc;
import wos.mobile.util.DateUtils;
import wos.mobile.util.LogUtil;
import wos.mobile.util.StringUtil;
import wos.mobile.widget.CustomDatePickerDialogFragment;
import wos.mobile.widget.CustomFab;
import wos.mobile.widget.DatePickerDialogFragment;
import wos.mobile.widget.DateTimePickerDialogFragment;
import wos.mobile.widget.LeftSlideRemoveListView;


public abstract class ActivityEx extends FragmentActivity {

    //#region 控件
    @BindView(id = "btnReturn")
    protected Button btnReturn;

    @BindView(id = "title")
    protected TextView title;

    @BindView(id = "btnAction")
    protected Button btnAction;

    @BindView(id = "titleBar")
    protected LinearLayout titleBar;

    @BindView(id = "layoutQuery")
    protected LinearLayout layoutQuery;

    @BindView(id = "txKeyword")
    protected EditText txKeyword;

    @BindView(id = "btnQuery")
    protected ImageButton btnQuery;

    @BindView(id = "listV")
    protected LeftSlideRemoveListView listV;

    @BindView(id = "btnFab")
    protected CustomFab btnFab;

    @BindView(id = "btnNext")
    protected CustomFab btnNext;

    @BindView(id = "btnPre")
    protected CustomFab btnPre;

    @BindView(id = "labVersion")
    protected TextView labVersion;
    @BindView(id = "labFactName")
    protected TextView labFactName;

    @BindView(id = "labStaffName")
    protected TextView labStaffName;


    protected ProgressDialog mProDialog; // 进度显示框
    //#endregion

    //#region 变量
    protected Context context;
    protected int pageNo = 1, pageSize = Constants.pageSize;

    protected long count = 0;

    protected String errMsg = "";

    /**
     * 屏幕尺寸
     */
    protected int contentWidth, contentHeight;

    protected ActivityResultLauncher<Intent> activityResultLauncher;

    //#endregion

    //#region 事件
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
//        initContentView(R.layout.base_login_state_ex);
//        MomApplication.getInstance().addActivity(this);
    }

    @Override
    public void onResume() {

        super.onResume();
        if (labFactName != null && StringUtil.isNotEmpty(Property.compName))
            labFactName.setText(Property.compName);
        if (labStaffName != null && StringUtil.isNotEmpty(Property.staffName))
            labStaffName.setText(Property.staffName);
        if (labVersion != null) labVersion.setText(getVersionName());

    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
//        View view = LayoutInflater.from(this).inflate(layoutResID, null);
//        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
//        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, relativeLayout.getChildAt(0).getId());
//        relativeLayout.addView(view, lp);
//        final Button btnReturn = (Button) findViewById("btnReturn);
//        if (btnReturn != null) {
//            btnReturn.setOnClickListener(new OnClickListener() {
//                public void onClick(View v) {
//                    String name = btnReturn.getContext().getClass().getName();
//                    Log.i("-----name", name);
//                    finish();
//                }
//            });
//        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean exitApp = false;
        if (keyCode == KeyEvent.KEYCODE_BACK
                || keyCode == KeyEvent.KEYCODE_HOME) {
            exitApp = super.onKeyDown(keyCode, event);
        } else {
            exitApp = super.onKeyDown(keyCode, event);
        }
        return exitApp;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    //#endregion

    //#region 尺寸

    /**
     * 展示区尺寸
     */
    protected void loadContentSize() {
        WindowManager windowManager = getWindowManager();
        android.view.Display display = windowManager.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        display.getMetrics(dm);
        int screenWidth = dm.widthPixels - 20;
        int screenHeight = dm.heightPixels;
        float scale = (float) screenWidth / 480;
        int chgValue = 50;
        switch (dm.heightPixels) {
            case 800:
                chgValue = 50;
                break;
            case 1184:
                chgValue = 46;
                break;
        }
        int height = screenHeight - titleBar.getHeight()
                - (int) (chgValue * scale);

        this.contentWidth = screenWidth;
        this.contentHeight = height;
    }
    //#endregion

    //#region 系统
    protected void onPause() {
        super.onPause();
        if (mProDialog != null)
            mProDialog.dismiss();
    }

    protected void exitSystem() {
        WosMobileApp.getInstance().exit();
    }

    /**
     * 初始化视图
     */
    public abstract void initView();
    //#endregion

    //#region 键盘

    /**
     * 隐藏键盘
     *
     * @param view 键盘宿主
     */
    protected void hideKeyword(View view) {
        if (view == null) return;
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    //#endregion

    //#region 界面单工交互

    //#region 进度
    public void closeProgressDialog() {
        if (mProDialog != null) {
            mProDialog.dismiss();
            mProDialog = null;
        }
    }

    public void closeProgressDialog(long delay) {
        CommonFunc.sleep(delay);
        if (mProDialog != null) {
            mProDialog.dismiss();
            mProDialog = null;
        }
    }

    public void showProgressDialog(int msgResId) {
        showProgressDialog(getString(msgResId));
    }

    public void showProgressDialog(int msgResId, boolean flag) {
        showProgressDialog(getString(msgResId), flag);
    }

    public void showProgressDialog(String message) {
        showProgressDialog(message, false);
    }

    public void showProgressDialog(String message, boolean flag) {
        if (mProDialog == null) {
            if (getParent() != null) {
                mProDialog = new ProgressDialog(getParent());
            } else {
                mProDialog = new ProgressDialog(this);
            }
        }

        if (!mProDialog.isShowing()) {
            mProDialog.dismiss();
            mProDialog.setMessage(message);
            mProDialog.setIndeterminate(false);
            mProDialog.setCancelable(flag);
            mProDialog.setIcon(R.drawable.waiting);
            mProDialog.show();
        } else {
            mProDialog.setMessage(message);
        }

        Thread mProgressThread = new Thread(() -> {
            try {
                int i = 0;
                while (i < 10) {
                    Thread.sleep(1000);
                    i++;
                    if (mProDialog == null) break;
                }
                if (i >= 10) LogUtil.i("服务器10秒内未相应");
                if (mProDialog != null) mProDialog.setCancelable(true);
            } catch (Exception e) {
                LogUtil.e(e.getMessage());
            }

        });

        mProgressThread.start();
    }
    //#endregion

    //#region 吐司
    public void showToast(CharSequence message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void showToast(int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
    }

    public void showToast(CharSequence message, int duration) {
        if (duration == 0 || duration == 1) {
            Toast.makeText(this, message, duration).show();
        } else {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }

    }

    public void showToast(int resId, int duration) {
        if (duration == 0 || duration == 1) {
            Toast.makeText(this, resId, duration).show();
        } else {
            Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
        }
    }
    //#endregion

    //#region 错误提示
    public void showErrMsg(int msgResId) {
        showErrMsg(getString(msgResId));
    }

    public void showErrMsg(String message) {
        if (StringUtil.isEmpty(message)) return;

        Builder builder = new Builder(this);
        builder.setIcon(R.drawable.error);
        builder.setMessage(message);
        builder.setTitle(R.string.Prompt);
        builder.setPositiveButton(R.string.OK, null);
        builder.create().show();
    }
    //#endregion

    //#endregion

    //#region 句柄消息

    /**
     * 获取句柄消息
     *
     * @param what 类型
     * @param obj  数据
     * @return 句柄消息
     */
    protected Message getMessage(int what, Object obj) {
        Message message = new Message();
        message.what = what;
        message.obj = obj;
        return message;
    }

    protected Message getMessage(EnumAction what, Object obj) {
        Message message = new Message();
        message.what = what.ordinal();
        message.obj = obj;
        return message;
    }
    //#endregion

    //#region 应用版本

    /**
     * 获取应用版本
     *
     * @return 版本号
     */
    protected String getVersionName() {
        try {
            PackageManager packageManager = getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            return packageInfo.versionName;
        } catch (Exception ex) {
            return "";
        }
    }
    //#endregion

    //#region 顶部按钮样式
    protected void configReturnButton(Button btn, int textResId, Bitmap bitmapLeft) {
        if (btn == null) return;
        btn.setText(textResId);

        Drawable drawable = new BitmapDrawable(getResources(), bitmapLeft); // 将Bitmap转换为Drawable
        btn.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null); // 设置左侧图片
        btn.setPadding(25, 0, 0, 0);

        btn.setVisibility(View.VISIBLE);

    }

    protected void configActionButton(Button btn, int textResId, Bitmap bitmapRight) {
        if (btn == null) return;
        btn.setText(textResId);

        Drawable drawable = new BitmapDrawable(getResources(), bitmapRight); // 将Bitmap转换为Drawable
        btn.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null); // 设置左侧图片
        btn.setPadding(15, 0, 10, 0);

        btn.setVisibility(View.VISIBLE);

    }
    //#endregion

    //#region 系统状态



    //#endregion

    //#region 日期与时间选择后回传给文本
    protected void setOnClickListener(List<View> nodeList, View.OnClickListener onClickListener) {
        if (nodeList == null || nodeList.isEmpty() || onClickListener == null) return;
        nodeList.forEach(node -> {
            node.setOnClickListener(onClickListener);
        });
    }

    protected DateTimePickerDialogFragment.OnSelectedDateTimeListener onSelectedDateTimeListener(TextView text) {
        return (date) -> {
            if (text == null) return;
            text.setText(DateUtils.formatDate(date, "yyyy-MM-dd HH:mm"));
        };
    }

    protected DatePickerDialogFragment.OnSelectedDateListener onSelectedDateListener(TextView text) {
        return (date) -> {
            if (text == null) return;
            text.setText(DateUtils.formatDate(date, "yyyy-MM-dd"));
        };
    }

    protected void showDataPick(Date currentDate, DatePickerDialogFragment.OnSelectedDateListener listener) {
        DatePickerDialogFragment datePickerDialogFragment = new DatePickerDialogFragment();
        if (listener != null) datePickerDialogFragment.setOnSelectedDateListener(listener);
        Bundle bundle = new Bundle();
        if (currentDate != null)
            bundle.putSerializable(DateTimePickerDialogFragment.CURRENT_DATE, DateUtils.getCalendar(currentDate));

        datePickerDialogFragment.setArguments(bundle);
        datePickerDialogFragment.show(getSupportFragmentManager(), CustomDatePickerDialogFragment.class.getSimpleName());
    }
    //#endregion

    //#region 控制控件显示状态

    /**
     * 设置控件状态
     *
     * @param visibility View.VISIBLE,View.INVISIBLE, View.GONE
     * @param views      控件
     */
    protected void setVisibility(int visibility, View... views) {
        if (views == null || views.length == 0) return;
        if (!Arrays.asList(View.VISIBLE, View.INVISIBLE, View.GONE).contains(visibility)) return;
        for (View view : views) {
            if (view == null) continue;
            view.setVisibility(visibility);
        }
    }
    //#endregion

    //#region 工具

    /**
     * 解析时间
     *
     * @param str 时间字符串
     * @return 时间
     */
    protected Date parseTime(String str) {
        if (StringUtil.isEmpty(str)) return null;
        try {
            return DateUtils.parseDate(str, "yyyy-MM-dd HH:mm");
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * 解析日期
     *
     * @param str 日期字符串
     * @return 日期
     */
    protected Date parseDate(String str) {
        if (StringUtil.isEmpty(str)) return null;
        try {
            return DateUtils.parseDate(str, "yyyy-MM-dd");
        } catch (Exception ex) {
            return null;
        }
    }
    //#endregion

    //#region 快捷菜单

    /**
     * 获取跨界菜单
     *
     * @param context context
     * @param view    视图
     * @param menuRes 菜单系统ID
     * @return 快捷菜单
     */
    protected PopupMenu getPopMenu(Context context, View view, int menuRes) {
        PopupMenu popupMenu = new PopupMenu(context, view);

        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(menuRes, popupMenu.getMenu());

        // 通过反射设置 PopupMenu 显示图标
        try {
            Field[] fields = popupMenu.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popupMenu);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceShowIcon = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                    setForceShowIcon.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 设置图标尺寸
        Menu menu = popupMenu.getMenu();
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            Drawable icon = item.getIcon();
            if (icon != null) {
                Bitmap bitmap = ((BitmapDrawable) icon).getBitmap();
                int targetWidth = 30; // 目标宽度
                int targetHeight = 30; // 目标高度
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true);
                item.setIcon(new BitmapDrawable(getResources(), resizedBitmap));
            }
        }

        return popupMenu;
    }

    //#endregion
}
