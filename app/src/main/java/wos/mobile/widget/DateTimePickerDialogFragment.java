package wos.mobile.widget;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.Date;

import wos.mobile.R;
import wos.mobile.annotation.AnnotateUtil;
import wos.mobile.annotation.BindView;
import wos.mobile.util.StringUtil;


public class DateTimePickerDialogFragment extends DialogFragment implements View.OnClickListener{

    //#region 变量
    public static final String CURRENT_DATE = "datepicker_current_date";

    Calendar calendar = Calendar.getInstance();

    @BindView(id = "timeSelectContain")
    private LinearLayout timeSelectContain;

    @BindView(id = "timeSelectTitle")
    private TextView timeSelectTitle;

    @BindView(id = "number_picker_year")
    NumberPicker yearPicker;

    @BindView(id = "number_picker_month")
    NumberPicker monthPicker;

    @BindView(id = "number_picker_day")
    NumberPicker datePicker;

    @BindView(id = "number_picker_hour")
    NumberPicker hourPicker;

    @BindView(id = "number_picker_minute")
    NumberPicker minutePicker;

    @BindView(id = "btnReturn")
    private Button btnReturn;

    @BindView(id = "btnConfirm")
    private Button btnConfirm;

    @BindView(id = "btnCurrent")
    private Button btnCurrent;
    //#endregion

    //#region 句柄
    private final Handler handler= new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message message) {
            timeSelectContain.setVisibility(View.VISIBLE);
            timeSelectTitle.setText(String.valueOf(message.obj));
        }
    };
    //#endregion


    //#region 事件
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
        Bundle bundle = getArguments();
        calendar=(Calendar) bundle.getSerializable(CURRENT_DATE);
    }

    private void initValue() {
        if (calendar==null) calendar=Calendar.getInstance();
        yearPicker.setMinValue(calendar.get(Calendar.YEAR)-1);
        yearPicker.setMaxValue(calendar.get(Calendar.YEAR)+1);
        yearPicker.setValue(calendar.get(Calendar.YEAR));
        yearPicker.setWrapSelectorWheel(false);  //关闭选择器循环

        //设置月份范围为1~12
        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(12);
        monthPicker.setValue(calendar.get(Calendar.MONTH) + 1);
        monthPicker.setWrapSelectorWheel(true);

        //日期限制存在变化，需要根据当月最大天数来调整
        datePicker.setMinValue(1);
        datePicker.setMaxValue(calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        datePicker.setValue(calendar.get(Calendar.DATE));
        datePicker.setWrapSelectorWheel(true);

        //24小时制，限制小时数为0~23
        hourPicker.setMinValue(0);
        hourPicker.setMaxValue(23);
        hourPicker.setValue(calendar.get(Calendar.HOUR_OF_DAY));
        hourPicker.setWrapSelectorWheel(true);

        //限制分钟数为0~59
        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(59);
        minutePicker.setValue(calendar.get(Calendar.MINUTE));
        minutePicker.setWrapSelectorWheel(true);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (inflater == null) {
            return super.onCreateView(inflater, container, savedInstanceState);
        }
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setDimAmount(0.8f);
        View view  = inflater.inflate(R.layout.dialog_fragment_date_time_picker,container,false);

        AnnotateUtil.initBindView(this);

        yearPicker.setOnValueChangedListener((picker,oldValue,newValue)->{
            int year=newValue,month=monthPicker.getValue();
            setDatePickerMaxValue(year,month);

        });
        monthPicker.setOnValueChangedListener((picker,oldValue,newValue)->{
            int year=newValue,month=monthPicker.getValue();
            setDatePickerMaxValue(year,month);
        });

        btnReturn.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);
        btnCurrent.setOnClickListener(this);

        initValue();
        return view;
    }

    private void setDatePickerMaxValue(int year,int month) {
        Calendar calendar=Calendar.getInstance();
        calendar.set(year,month-1,1);
        if (calendar==null) return;
        datePicker.setMinValue(1);
        datePicker.setMaxValue(calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onClick(View view) {
        if (view==btnConfirm) {
            int year=yearPicker.getValue(),month=monthPicker.getValue(),day=datePicker.getValue(),hour=hourPicker.getValue(),minute=minutePicker.getValue();
            calendar.set(year,month-1,day,hour,minute);
            if (onSelectedDateListener!=null) onSelectedDateListener.onSelectedDate(calendar.getTime());
        } else if (view==btnCurrent) {
            if (onSelectedDateListener!=null) onSelectedDateListener.onSelectedDate(new Date());
        }
        dismiss();
    }
    //#endregion

    //#region 接口
    public interface OnSelectedDateTimeListener {
        void onSelectedDate(Date date);
    }

    private OnSelectedDateTimeListener onSelectedDateListener;

    public OnSelectedDateTimeListener getOnSelectedDateListener() {
        return onSelectedDateListener;
    }

    public void setOnSelectedDateListener(OnSelectedDateTimeListener onSelectedDateListener) {
        this.onSelectedDateListener = onSelectedDateListener;
    }

    public void setTitle(String title) {
        if (StringUtil.isNotEmpty(title)) {
            Message message=new Message();
            message.obj=title;
            handler.sendMessage(message);
        }
    }

    //#endregion
}
