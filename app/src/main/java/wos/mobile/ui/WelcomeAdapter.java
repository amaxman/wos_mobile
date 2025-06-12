package wos.mobile.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import wos.mobile.R;
import wos.mobile.entity.EnumAction;
import wos.mobile.entity.auth.PermissionRestEntity;
import wos.mobile.service.BasicRestService;
import wos.mobile.util.StringUtil;


public class WelcomeAdapter extends BasicAdapter<PermissionRestEntity> {
    private Handler handler = null;

    public WelcomeAdapter(Context context, List<PermissionRestEntity> list,
                          Handler handler, int width, int height) {
        super(context,list,width,height);
        this.handler = handler;
    }


    private final Handler handlerAdapter = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message message) {
            if (message==null || message.obj==null) return;
            JSONObject json=(JSONObject) message.obj;
            if (json==null) return;

            Bitmap btImg=json.getObject("btImg",Bitmap.class);
            ImageButton btnPermission=json.getObject("btnPermission",ImageButton.class);
            String funcCode=json.getString("funCode");

            if (btImg==null) btImg=getImageFromAssetsFile(funcCode+".png");

            if (btImg == null) {
                btnPermission.setBackgroundResource(android.R.drawable.btn_default);
            } else {
                btnPermission.setImageBitmap(btImg);
            }



        }
    };

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PermissionRestEntity permission = getItem(position);
        if (permission == null)  return null;

        //#region 获取ViewHolder
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.permission_item, null);
            holder = new ViewHolder();
            holder.layout = (LinearLayout) convertView
                    .findViewById(R.id.layout);
            holder.btnPermission = (ImageButton) convertView
                    .findViewById(R.id.btnPermission);
            holder.btnTitle=(Button)convertView.findViewById(R.id.btnTitle);
            convertView.setTag(holder);
            holder.permission=permission;
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //#endregion

        //#region ViewHolder尺寸
        LayoutParams params = holder.layout.getLayoutParams();
        if (params != null) {
            params.width = width / 3;
        } else {
            params = new AbsListView.LayoutParams(width / 3-3, 400);
        }

        holder.layout.setLayoutParams(params);
        //#endregion

        //#region ImageButton布局
        LayoutParams permissionImgViewLayoutParams = holder.btnPermission.getLayoutParams();
        if (permissionImgViewLayoutParams != null) {
            permissionImgViewLayoutParams.width = 64*3;
            permissionImgViewLayoutParams.height = 64*3;
        }
        holder.btnPermission.setLayoutParams(permissionImgViewLayoutParams);
        //#endregion

        //#region


        ExecutorService executorService = Executors.newFixedThreadPool(3);
        executorService.execute(() -> {
            String code = permission.getCode(),imagePath= permission.getImagePath();

            Bitmap btImg = null;
            if (StringUtil.isNotEmpty(imagePath)) {
                try {
                    String imageFilePath=downloadFile(BasicRestService.restServer+imagePath,context.getFilesDir().getPath()+"/images/welcome");
                    File file=new File(imageFilePath);
                    if (file.exists()) {
                        btImg=BitmapFactory.decodeFile(imageFilePath);
                    }

                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }

            }

            JSONObject json=new JSONObject();
            json.put("btImg",btImg);
            json.put("btnPermission",holder.btnPermission);
            json.put("code",code);
            Message message=new Message();
            message.obj=json;
            handlerAdapter.sendMessage(message);



        });
        executorService.shutdown();
        //#endregion

        String funcCode = permission.getCode();

        OnClickListener onClickListener=launchByFuncCode(EnumAction.launch, funcCode);
        holder.btnTitle.setText(permission.getTitle());
        holder.layout.setOnClickListener(onClickListener);
        holder.btnTitle.setOnClickListener(onClickListener);
        holder.btnPermission.setOnClickListener(onClickListener);

        return convertView;
    }

    private OnClickListener launchByFuncCode(final EnumAction what,final String funcCode) {
        return (v)->{
            Message message=Message.obtain(handler, what.ordinal(), funcCode);
            handler.sendMessage(message);
        };
    }

    static class ViewHolder {
        LinearLayout layout;
        ImageButton btnPermission;
        Button btnTitle;
        PermissionRestEntity permission;
    }
}
