package wos.mobile.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import java.util.List;

import wos.mobile.R;
import wos.mobile.ui.BasicAdapter;

public abstract class LeftSlideRemoveAdapter<T> extends BasicAdapter<T> {
    public OnItemRemoveListener mListener;

    public LeftSlideRemoveAdapter(Context context, List<T> list, int width, int height) {
        super(context, list, width, height);
    }

    @Override
    public final View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.view_left_slide_remove, parent, false);

            holder = new ViewHolder();
            holder.viewContent = (RelativeLayout) convertView.findViewById(R.id.viewContent);
            holder.btnRemove = (Button) convertView.findViewById(R.id.btnRemove);
            convertView.setTag(holder);

            // viewChild是实际的界面
            holder.viewChild = getSubView(position, null, parent);
            holder.viewContent.addView(holder.viewChild);
        } else {
            holder = (ViewHolder) convertView.getTag();
            getSubView(position, holder.viewChild, parent);
        }
        holder.btnRemove.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onItemRemove(position);
                    notifyDataSetChanged();
                }
            }
        });

        return convertView;
    }

    public abstract View getSubView(int position, View convertView, ViewGroup parent);

    public static class ViewHolder {
        RelativeLayout viewContent;
        View viewChild;
        Button btnRemove;
    }

    public static interface OnItemRemoveListener {
        public void onItemRemove(int position);
    }


}



