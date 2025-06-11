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

import wos.mobile.entity.workOrder.WorkOrderRestEntity;
import wos.mobile.R;
import wos.mobile.util.DateUtils;
import wos.mobile.util.StringUtil;
import wos.mobile.widget.LeftSlideRemoveAdapter;

public class WorkOrderAdapter extends LeftSlideRemoveAdapter<WorkOrderRestEntity> {
    //#region 变量
    private Handler handler;

    private boolean showMore=true;
    //#endregion
    public WorkOrderAdapter(Context context, List<WorkOrderRestEntity> list, int width, int height, Handler handler, boolean showMore) {
        super(context, list, width, height);
        this.handler=handler;
        this.showMore=showMore;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getSubView(int position, View convertView, ViewGroup parent) {
        WorkOrderRestEntity entity=getItem(position);
        if (entity==null) return null;

        //#region 获取ViewHolder
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.work_order_item, parent,false);

            holder = new ViewHolder();
            holder.labMain = (TextView) convertView.findViewById(R.id.labMain);
            holder.labContent = (TextView) convertView.findViewById(R.id.labContent);
            holder.labStartTime = (TextView) convertView.findViewById(R.id.labStartTime);
            holder.labEndTime = (TextView) convertView.findViewById(R.id.labEndTime);
            holder.btnModify = (Button) convertView.findViewById(R.id.btnModify);
            holder.btnDeleteItem = (Button) convertView.findViewById(R.id.btnDeleteItem);

            holder.layoutArrowRight=(LinearLayout) convertView.findViewById(R.id.layoutArrowRight);
            if (!showMore)  {
                holder.layoutArrowRight.setVisibility(View.GONE);
                holder.layoutArrowRight.setOnClickListener((v)->{
                    WorkOrderRestEntity _entity=holder.entity;
                    if (_entity==null) return;
                    Message message=new Message();
                    message.what= 2;
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

        holder.labMain.setText(entity.getTitle());
        holder.labContent.setText(entity.getContent());
        holder.labStartTime.setText(DateUtils.formatDate(entity.getStarTime(),"yyyy-MM-dd HH:mm"));
        holder.labEndTime.setText(DateUtils.formatDate(entity.getEndTime(),"yyyy-MM-dd HH:mm"));


        holder.btnModify.setOnClickListener((v)->{
            Message message=getMessage(2,entity.getId());
            handler.sendMessage(message);
        });
        holder.btnDeleteItem.setOnClickListener((v)->{
            Message message=getMessage(1,entity);
            handler.sendMessage(message);
        });

        holder.entity=entity;

        //#endregion

        return convertView;
    }

    static class ViewHolder {
        TextView labMain,labContent,labStartTime,labEndTime;
        LinearLayout layoutArrowRight;
        WorkOrderRestEntity entity;

        Button btnModify,btnDeleteItem;

    }
}
