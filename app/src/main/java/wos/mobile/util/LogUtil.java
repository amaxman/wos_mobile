package wos.mobile.util;

import android.util.Log;

import java.io.File;

import wos.mobile.entity.Property;


public class LogUtil {
	private static final String tag = "guardian_wolf";
	private static final boolean debug = true;

	public static void e(String msg) {
		if (debug) {
			Log.e(tag, msg);
			writerLog(msg);
		}
	}

	public static void w(String msg) {
		if (debug) {
			Log.w(tag, msg);
			writerLog(msg);
		}
	}

	public static void i(String msg) {
		if (debug) {
			Log.i(tag, msg);
			writerLog(msg);
		}
	}

	public static void d(String msg) {
		if (debug) {
			Log.d(tag, msg);
			writerLog(msg);
		}
	}
	
	public static void writerLog(String msg) {
		try {
			String foldPath= Property.context.getExternalCacheDir() +"/log/";
			File fileFold=new File(foldPath);
			if (!fileFold.exists()) fileFold.mkdirs();

			String path = foldPath+tag+".txt";
			File file = new File(path);
			long fileSizel = FileSizeUtil.getFileSize(file);
			double sizeStr = FileSizeUtil.FormetFileSize(fileSizel,FileSizeUtil.SIZETYPE_MB);
			if (sizeStr >= 10){
				FileSizeUtil.deleteFile(path);
			}
			LogWriter mLogWriter = LogWriter.open(path);
			mLogWriter.print(msg); 
			mLogWriter.close();
	    } catch (Exception e) {  
	        e.printStackTrace();
	    }  
	}
	
}
