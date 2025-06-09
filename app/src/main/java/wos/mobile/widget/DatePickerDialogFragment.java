package wos.mobile.widget;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.NumberPicker;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.Date;

import wos.mobile.R;
import wos.mobile.annotation.AnnotateUtil;
import wos.mobile.annotation.BindView;

public class DatePickerDialogFragment extends DialogFragment implements View.OnClickListener{

    //#region 变量
    public static final String CURRENT_DATE = "datepicker_current_date";

    Calendar calendar = Calendar.getInstance();

    @BindView(id = "number_picker_year")
    NumberPicker yearPicker;

    @BindView(id = "number_picker_month")
    NumberPicker monthPicker;

    @BindView(id = "number_picker_day")
    NumberPicker datePicker;

    @BindView(id = "btnReturn")
    private Button btnReturn;

    @BindView(id = "btnConfirm")
    private Button btnConfirm;

    @BindView(id = "btnCurrent")
    private Button btnCurrent;
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

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (inflater == null) {
            return super.onCreateView(inflater, container, savedInstanceState);
        }
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setDimAmount(0.8f);
        View view  = inflater.inflate(R.layout.dialog_fragment_date_picker,container,false);

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
            int year=yearPicker.getValue(),month=monthPicker.getValue(),day=datePicker.getValue();
            calendar.set(year,month-1,day);
            if (onSelectedDateListener!=null) onSelectedDateListener.onSelectedDate(calendar.getTime());
        } else if (view==btnCurrent) {
            if (onSelectedDateListener!=null) onSelectedDateListener.onSelectedDate(new Date());
        }
        dismiss();
    }
    //#endregion

    //#region 接口
    public interface OnSelectedDateListener {
        void onSelectedDate(Date date);
    }

    private OnSelectedDateListener onSelectedDateListener;

    public OnSelectedDateListener getOnSelectedDateListener() {
        return onSelectedDateListener;
    }

    public void setOnSelectedDateListener(OnSelectedDateListener onSelectedDateListener) {
        this.onSelectedDateListener = onSelectedDateListener;
    }

    //#endregion
}
