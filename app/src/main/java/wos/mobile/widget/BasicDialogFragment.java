package wos.mobile.widget;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import wos.mobile.R;
import wos.mobile.annotation.BindView;
import wos.mobile.entity.EnumAction;
import wos.mobile.util.LogUtil;


public abstract class BasicDialogFragment extends DialogFragment {
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

    protected Context context;
    //#endregion

    //#region
    public BasicDialogFragment(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(true);

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
                params.height = (int) (getResources().getDisplayMetrics().heightPixels * 0.8); // 80% 屏幕高度
                window.setAttributes(params);
            }
        }
    }

    protected void hideKeyword(View view) {
        if (view==null) return;
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(),0);
    }

    protected Message getMessage(EnumAction what, Object obj) {
        Message message=new Message();
        message.what=what.ordinal();
        message.obj=obj;
        return message;
    }

    //#region 顶部按钮样式
    protected void configReturnButton(Button btn, int textResId, Bitmap bitmapLeft) {
        if (btn==null) return;
        btn.setText(textResId);

        Drawable drawable = new BitmapDrawable(getResources(), bitmapLeft); // 将Bitmap转换为Drawable
        btn.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null); // 设置左侧图片
        btn.setPadding(25,0,0,0);

    }

    protected void configActionButton(Button btn, int textResId, Bitmap bitmapRight) {
        if (btn==null) return;
        btn.setText(textResId);

        Drawable drawable = new BitmapDrawable(getResources(), bitmapRight); // 将Bitmap转换为Drawable
        btn.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null); // 设置左侧图片
        btn.setPadding(15,0,10,0);

    }
    //#endregion

    //#region 对话框
    protected void closeProgressDialog() {
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
            mProDialog = new ProgressDialog(context);
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
    protected void showToast(CharSequence message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    protected void showToast(CharSequence message,int duration) {
        if (duration==0 || duration==1) {
            Toast.makeText(context, message, duration).show();
        } else {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }

    }

    protected void showToast(int resId) {
        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
    }
    //#endregion

    //#region
    @Nullable
    public View getCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState,int resourceId) {
        if (inflater == null) {
            return super.onCreateView(inflater, container, savedInstanceState);
        }

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setDimAmount(0.9f);
        View view = inflater.inflate(resourceId, container, false);
        return view;
    }
    //#endregion

    protected abstract void initView();
}
