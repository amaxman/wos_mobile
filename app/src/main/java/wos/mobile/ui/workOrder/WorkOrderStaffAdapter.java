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
import wos.mobile.widget.LeftSlideRemoveAdapter;

public class WorkOrderStaffAdapter extends LeftSlideRemoveAdapter<WorkOrderStaffRestEntity> {
    //#region 变量
    private Handler handler;

    private boolean showMore=true;
    //#endregion
    public WorkOrderStaffAdapter(Context context, List<WorkOrderStaffRestEntity> list, int width, int height, Handler handler, boolean showMore) {
        super(context, list, width, height);
        this.handler=handler;
        this.showMore=showMore;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getSubView(int position, View convertView, ViewGroup parent) {
        WorkOrderStaffRestEntity entity=getItem(position);
        if (entity==null) return null;

        //#region 获取ViewHolder
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.work_order_staff_item, parent,false);

            holder = new ViewHolder();
            holder.labStaffName = (TextView) convertView.findViewById(R.id.labStaffName);
            holder.labProgress = (TextView) convertView.findViewById(R.id.labProgress);

            holder.btnDeleteItem = (Button) convertView.findViewById(R.id.btnDeleteItem);

            holder.layoutArrowRight=(LinearLayout) convertView.findViewById(R.id.layoutArrowRight);
            if (!showMore)  {
                holder.layoutArrowRight.setVisibility(View.GONE);
                holder.layoutArrowRight.setOnClickListener((v)->{
                    WorkOrderStaffRestEntity _entity=holder.entity;
                    if (_entity==null) return;
                    Message message=new Message();
                    message.what= EnumAction.edit_activity.ordinal();
                    message.obj=_entity.getId();
                    handler.sendMessage(message);
                });
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //#endregion

        //#region 绑定数据

        holder.labStaffName.setText(entity.getStaffName());
        if (entity.getWorkProgress()==null || entity.getWorkProgress()==0) {
            holder.labProgress.setText(R.string.work_order_staff_progress_waiting);
        } else if (entity.getWorkProgress()==100) {
            holder.labProgress.setText(R.string.work_order_staff_progress_completed);
        } else {
            holder.labProgress.setText(String.valueOf(entity.getWorkProgress())+"%");
        }


        holder.btnDeleteItem.setOnClickListener((v)->{
            Message message=getMessage(EnumAction.delete,entity);
            handler.sendMessage(message);
        });

        holder.entity=entity;

        //#endregion

        return convertView;
    }

    static class ViewHolder {
        TextView labStaffName,labProgress;
        LinearLayout layoutArrowRight;
        WorkOrderStaffRestEntity entity;

        Button btnDeleteItem;

    }
}
