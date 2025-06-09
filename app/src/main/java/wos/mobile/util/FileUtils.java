package wos.mobile.util;

import android.net.Uri;
import android.os.Environment;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


public class FileUtils {
	private String SDPATH;

	public String getSDPATH() {
		return SDPATH;
	}

	public FileUtils(String savePath) {
		// 得到当前外部存储设备的目录( /SDCARD )
		SDPATH = Environment.getExternalStorageDirectory() + "/";
		if (savePath == null) {
			SDPATH = Environment.getExternalStorageDirectory() + "/";
		} else if (savePath == "") {
			SDPATH = Environment.getExternalStorageDirectory() + "/";
		} else {
			SDPATH = savePath;
		}
	}

	/**
	 * 在SD卡上创建文件
	 * 
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public File createSDFile(String fileName) throws IOException {
		File file = new File(fileName);
		file.createNewFile();
		return file;
	}

	/**
	 * 在SD卡上创建目录
	 * 
	 * @param dirName
	 * @return
	 */
	public File createSDDir(String dirName) {
		File dir = new File(dirName);
		dir.mkdirs();
		return dir;
	}

	/**
	 * 判断SD卡上的文件夹是否存在
	 * 
	 * @param fileName
	 * @return
	 */
	public boolean isFileExist(String fileName) {
		File file = new File(fileName);
		return file.exists();
	}

	/**
	 * 将一个InputStream里面的数据写入到SD卡中
	 * 
	 * @param path
	 * @param fileName
	 * @param input
	 * @return
	 */
	public File write2SDFromInput(String path, String fileName, byte[] input) {
		File file = null;
		OutputStream output = null;
		try {
			createSDDir(path);
			file = createSDFile(path + fileName);
			/*
			 * output = new FileOutputStream(file); byte[] buffer = new
			 * byte[FILESIZE]; while((input.read(buffer)) != -1){
			 * output.write(buffer); } output.flush(); output.close();
			 */
			BufferedOutputStream bos = new BufferedOutputStream(
					new FileOutputStream(file));
			bos.write(input);
			bos.flush();
			bos.close();

			return file;
		} catch (Exception e) {
			e.printStackTrace();
			file.deleteOnExit();
		} finally {
			try {
				if (output != null)
					output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static File getFileFromUri(Uri uri) {
		if ("file".equals(uri.getScheme())) {
			return new File(uri.getPath());
		}
		return null;
	}

	public static boolean deleteByUri(Uri uri) {
		if (uri==null) return false;
		File file=getFileFromUri(uri);
		if (!file.exists()) return true;
		try {
			return file.delete();
		} catch (Exception ex) {
			LogUtil.e(ex.getMessage());
			return false;
		}
	}

	/**
	 * 	创建目录
	 * @param path 目录
	 * @return 是否创建成功
	 */
	public static boolean mkdirs(String path) {
		File fileFold=new File(path);
		if (!fileFold.exists()) fileFold.mkdirs();
		return fileFold.exists();
	}

}
