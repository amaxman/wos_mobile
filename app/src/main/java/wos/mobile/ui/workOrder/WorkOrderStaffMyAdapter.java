package wos.mobile.ui.workOrder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import wos.mobile.R;
import wos.mobile.entity.EnumAction;
import wos.mobile.entity.workOrder.WorkOrderStaffRestEntity;
import wos.mobile.util.DateUtils;
import wos.mobile.util.StringUtil;
import wos.mobile.widget.LeftSlideRemoveAdapter;

public class WorkOrderStaffMyAdapter extends LeftSlideRemoveAdapter<WorkOrderStaffRestEntity> {
    //#region 变量
    private Handler handler;
    //#endregion
    public WorkOrderStaffMyAdapter(Context context, List<WorkOrderStaffRestEntity> list, int width, int height, Handler handler) {
        super(context, list, width, height);
        this.handler=handler;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getSubView(int position, View convertView, ViewGroup parent) {
        WorkOrderStaffRestEntity entity=getItem(position);
        if (entity==null) return null;

        //#region 获取ViewHolder
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.work_order_staff_item_my, parent,false);

            holder = new ViewHolder();

            holder.labTitle = (TextView) convertView.findViewById(R.id.labTitle);
            holder.labContent = (TextView) convertView.findViewById(R.id.labContent);
            holder.labStartTime = (TextView) convertView.findViewById(R.id.labStartTime);
            holder.labEndTime = (TextView) convertView.findViewById(R.id.labEndTime);
            holder.labCate = (TextView) convertView.findViewById(R.id.labCate);
            holder.labLevel = (TextView) convertView.findViewById(R.id.labLevel);
            holder.labProgress = (TextView) convertView.findViewById(R.id.labProgress);

            holder.btnModifyProgress = (Button) convertView.findViewById(R.id.btnModifyProgress);
            holder.btnComplete = (Button) convertView.findViewById(R.id.btnComplete);


            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //#endregion

        //#region 绑定数据

        //labTitle,labContent,labStartTime,labEndTime,labCate,labLevel,labProgress
        holder.labTitle.setText(entity.getTitle());
        holder.labContent.setText(entity.getContent());
        holder.labStartTime.setText(DateUtils.formatDate(entity.getStartTime(),"yyyy-MM-dd HH:mm"));
        holder.labEndTime.setText(DateUtils.formatDate(entity.getEndTime(),"yyyy-MM-dd HH:mm"));
        holder.labCate.setText(entity.getCateName());
        holder.labLevel.setText(entity.getLevelName());
        if (entity.getWorkProgress()==null || entity.getWorkProgress()==0) {
            holder.labProgress.setText(R.string.work_order_staff_progress_waiting);
        } else if (entity.getWorkProgress()==100) {
            holder.labProgress.setText(R.string.work_order_staff_progress_completed);
        } else {
            holder.labProgress.setText(String.valueOf(entity.getWorkProgress())+"%");
        }

        if (StringUtil.equalsAnyIgnoreCase(entity.getCateCode(),"yes_no")) {
            holder.btnComplete.setOnClickListener((v)->{
                Message message=getMessage(EnumAction.edit,entity.getId());
                handler.sendMessage(message);
            });
            holder.btnComplete.setVisibility(View.VISIBLE);
            holder.btnModifyProgress.setVisibility(View.GONE);
        } else  if (StringUtil.equalsAnyIgnoreCase(entity.getCateCode(),"progress")) {
            holder.btnModifyProgress.setOnClickListener((v)->{
                Message message=getMessage(EnumAction.edit_activity,entity);
                handler.sendMessage(message);
            });
            holder.btnComplete.setVisibility(View.GONE);
            holder.btnModifyProgress.setVisibility(View.VISIBLE);
        } else {
            holder.btnComplete.setOnClickListener((v)->{
                Message message=getMessage(EnumAction.edit,entity.getId());
                handler.sendMessage(message);
            });
            holder.btnComplete.setVisibility(View.VISIBLE);
            holder.btnModifyProgress.setVisibility(View.GONE);
        }



        holder.entity=entity;

        //#endregion

        return convertView;
    }

    static class ViewHolder {
        TextView labTitle,labContent,labStartTime,labEndTime,labCate,labLevel,labProgress;
        WorkOrderStaffRestEntity entity;
        Button btnModifyProgress,btnComplete;

    }
}
