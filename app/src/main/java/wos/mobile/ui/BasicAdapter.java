package wos.mobile.ui;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Message;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import wos.mobile.R;
import wos.mobile.entity.EnumAction;
import wos.mobile.util.LogUtil;
import wos.mobile.util.StringUtil;
import wos.mobile.util.DownloadHelper;

public abstract class BasicAdapter<T> extends BaseAdapter {
    //#region 变量
    protected Context context = null;

    protected List<T> list;

    protected int width, height;

    protected LayoutInflater mInflater;
    //#endregion


    public BasicAdapter(Context context,
                        List<T> list,
                        int width,
                        int height) {
        this.context = context;
        this.list = list;
        this.width = width;
        this.height = height;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        if (list == null)
            return 0;
        return list.size();
    }

    @Override
    public T getItem(int position) {
        if (getCount() == 0)
            return null;
        return list.get(position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public void updateData(List<T> list) {
        this.list=list;
        notifyDataSetChanged();
    }

    /**
     * 从assets目录获取图片
     * @param fileName 文件名
     * @return 图片Bitmap
     */
    protected Bitmap getImageFromAssetsFile(String fileName) {
        Bitmap image = null;
        AssetManager am = context.getResources().getAssets();
        try {
            InputStream is = am.open(fileName);
            image = BitmapFactory.decodeStream(is);

            is.close();
        } catch (IOException e) {
            LogUtil.e(e.getMessage());
        }
        return image;

    }

    protected String downloadFile(String url,String destFolder) throws Exception {
        DownloadHelper downloadHelper=new DownloadHelper();
        String destFileName=DownloadHelper.GetRemoteFileName(url);
        String path = destFolder+"/"+destFileName;
        if (StringUtil.isNotEmpty(path)) {
            File file=new File(path);
            if (file.isFile() && file.exists()) {
                return path;
            }
        }
        int result=downloadHelper.DownFile(url,destFolder);
        if (result==-1) {
            throw new Exception(context.getString(R.string.download_file_failure));
        } else {
            return path;
        }
    }

    protected Bitmap getBitmapFromUrl(String imageUrl) {
        Bitmap bitmap = null;
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
        } catch (IOException e) {
            return null;
        }
        return bitmap;
    }

    protected Message getMessage(EnumAction what, Object obj) {
        Message message=new Message();
        message.what=what.ordinal();
        message.obj=obj;
        return message;
    }
}
