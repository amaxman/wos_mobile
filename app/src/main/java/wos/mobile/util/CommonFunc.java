package wos.mobile.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import wos.mobile.entity.ACache;

public class CommonFunc {

	public static final String CONFIG = "USER_CONFIG";

	public static final String Config_UserId = "userId";
	public static final String Config_CompId = "compId";
	public static final String Config_SessionId = "sessionId";
	public static final String Config_StaffName = "staffName";
	public static final String Config_CompName = "compName";


	public static String getLocalIpAddress() {
		String ipAddress = "0.0.0.0";
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						ipAddress = inetAddress.getHostAddress().toString();
						if (!ipAddress.contains(":"))
							return ipAddress;
					}
				}
			}
		} catch (SocketException ex) {

		}
		return ipAddress;
	}
	//DevicePolicyManagewr.getWifiMacAddress()
	public static String GetMac(Context context, ACache cache){
		String mac_s= "";
		try {
			byte[] mac;
			NetworkInterface ne=NetworkInterface.getByInetAddress(InetAddress.getByName(getLocalIpAddress()));
			mac = ne.getHardwareAddress();
			mac_s = byte2hex(mac);
		} catch (Exception e) {
			e.printStackTrace();
		}
		cache.put("macAddress",mac_s);
		return mac_s;
	}

	public static  String byte2hex(byte[] b) {
		StringBuffer hs = new StringBuffer(b.length);
		String stmp = "";
		int len = b.length;
		for (int n = 0; n < len; n++) {
			stmp = Integer.toHexString(b[n] & 0xFF);
			if (stmp.length() == 1)
				hs = hs.append("0").append(stmp);
			else {
				if ( n < len - 1 ){
					hs = hs.append(stmp).append(":");
				}else {
					hs = hs.append(stmp);
				}

			}
		}
		return String.valueOf(hs);
	}


	public static String GetMacAddr(Context context) {
//		String macAddr = "00-00-00-00-00-00-00-00";
//		
//			WifiManager wifi = (WifiManager) context
//					.getSystemService(android.content.Context.WIFI_SERVICE);
//			WifiInfo info = wifi.getConnectionInfo();
//			macAddr = info.getMacAddress();
//			return macAddr;
		String macSerial = null;
		String str = "";
		try {
			Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address ");
			InputStreamReader ir = new InputStreamReader(pp.getInputStream());
			 LineNumberReader input = new LineNumberReader(ir);
			 for (; null != str;){
				 str = input.readLine();
				 if (str != null) {
					 macSerial = str.trim();// 去空格
					 break;
				 }
			 }
		 } catch (IOException ex) {
			 ex.printStackTrace();}
			 return macSerial;

	}

	private static HashMap<String, Long> mpHandelId = new HashMap<String, Long>();

	public static void PutHandelId(String handleName, long handleId) {
		mpHandelId.put(handleName, handleId);
	}

	public static long GetHandelId(String handleName) {
		if (mpHandelId.containsKey(handleName)) {
			return mpHandelId.get(handleName);
		} else {
			return 0;
		}
	}

	public static String GetCSharpString(String str) {
		if (str == null)
			return "";
		if (str.equals("null") || str.equals("&nbsp;") || str == "null")
			return "";
		str = str.trim();
		return str;
	}

	/*
	 * 是否为数字
	 */
	public static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches()) {
			return false;
		}
		return true;
	}

	public static String ClearSameItem(String str, String regularExpression) {
		String[] arr = str.split(regularExpression);
		ArrayList<String> arrList = new ArrayList<String>();
		for (String item : arr) {
			if (item == null) {
				continue;
			}
			item = item.trim();
			if (item.equals("")) {
				continue;
			}

			if (arrList.contains(item)) {
				continue;
			}

			arrList.add(item);
		}
		return arrList.toString();
	}

	public static Bitmap AddNum2Img(Bitmap srcImg, int total) {
		if (total <= 0)
			return srcImg;

		int width = srcImg.getWidth();

		Canvas canvas = new Canvas(srcImg);

		Paint countPaint = new Paint(Paint.ANTI_ALIAS_FLAG
				| Paint.DEV_KERN_TEXT_FLAG);
		countPaint.setColor(Color.RED);
		countPaint.setTextSize(30f);

		countPaint.setTypeface(Typeface.DEFAULT_BOLD);
		String numValues = "";
		if (total > 99) {
			numValues = "99+";
			canvas.drawText(numValues, width - 52, 29, countPaint);
		} else if (total >= 10) {
			numValues = String.valueOf(total);
			canvas.drawText(numValues, width - 36, 29, countPaint);
		} else {
			numValues = String.valueOf(total);
			canvas.drawText(numValues, width - 20, 29, countPaint);
		}
		return srcImg;

	}

	public static Bitmap GetNotifyWithTotal(Resources res, int resId, int total) {
		if (total <= 0)
			return null;
		Bitmap icon;
		Drawable iconDrawable = res.getDrawable(resId);
		if (iconDrawable instanceof BitmapDrawable) {
			BitmapDrawable bd = (BitmapDrawable) iconDrawable;
			icon = bd.getBitmap();
		} else {
			return null;
		}
		int iconSize = (int) res.getDimension(android.R.dimen.app_icon_size);
		Bitmap contactIcon = Bitmap.createBitmap(iconSize, iconSize,
				Config.ARGB_8888);

		Canvas canvas = new Canvas(contactIcon);

		// 拷贝图片
		Paint iconPaint = new Paint();
		iconPaint.setDither(true);// 防抖动
		iconPaint.setFilterBitmap(true);// 用来对Bitmap进行滤波处理，这样，当你选择Drawable时，会有抗锯齿的效果
		Rect src = new Rect(0, 0, icon.getWidth(), icon.getHeight());
		Rect dst = new Rect(0, 0, iconSize, iconSize);
		canvas.drawBitmap(icon, src, dst, iconPaint);

		/*
		 * Paint pCircleBig = new Paint(); pCircleBig.setColor(Color.RED);
		 * pCircleBig.setDither(true); pCircleBig.setAntiAlias(true);
		 * pCircleBig.setFilterBitmap(true); canvas.drawCircle(48, 23, 18,
		 * pCircleBig);
		 */

		// 启用抗锯齿和使用设备的文本字距
		Paint countPaint = new Paint(Paint.ANTI_ALIAS_FLAG
				| Paint.DEV_KERN_TEXT_FLAG);
		countPaint.setColor(Color.RED);
		countPaint.setTextSize(30f);

		countPaint.setTypeface(Typeface.DEFAULT_BOLD);
		String numValues = "";
		if (total > 99) {
			numValues = "99+";
			canvas.drawText(numValues, iconSize - 38, 29, countPaint);
		} else if (total >= 10) {
			numValues = String.valueOf(total);
			canvas.drawText(numValues, iconSize - 36, 29, countPaint);
		} else {
			numValues = String.valueOf(total);
			canvas.drawText(numValues, iconSize - 20, 29, countPaint);
		}

		return contactIcon;
	}

	/**
	 * 休眠
	 *
	 * @author Tyr Tao
	 * @param milSeconds
	 *            休眠的毫秒
	 */
	public static void SleepTime(int milSeconds) {
		try {
			Thread.sleep(milSeconds);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 日期转化为时间
	 *
	 * @author Tyr Tao
	 * @param curDttm
	 * @return
	 */
	public static String ConvertData2Str(Date curDttm) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String str = sdf.format(curDttm);
		return str;
	}

	public static String ConvertData2Str(Date curDttm, String dataFormat) {
		SimpleDateFormat sdf = new SimpleDateFormat(dataFormat);
		String str = sdf.format(curDttm);
		return str;
	}

	/**
	 * 将字符串转换为日期
	 *
	 * @author Tyr Tao
	 * @param str
	 * @param dateFormat
	 * @return
	 */
	public static Date ConvertStr2Date(String str, String dateFormat) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
			Date dt = sdf.parse(str);
			return dt;
		} catch (Exception ex) {
			return null;
		}
	}

	/**
	 * 获取当前时间
	 *
	 * @author Tyr Tao
	 * @return
	 */
	public static String GetCurDttm() {
		return ConvertData2Str(new Date());
	}

	public static String GetCurDttm(String dataFormat) {
		return ConvertData2Str(new Date(), dataFormat);
	}

	public static String getGuid() {
		UUID guid = UUID.randomUUID();
		return guid.toString();
	}

	public static String getFileExtension(String fileName) {
		if (StringUtil.isEmpty(fileName)) return "";
		if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
			return fileName.substring(fileName.lastIndexOf(".")+1);
		else return "";
	}

	public static void sleep(long millTime) {
		try {
			Thread.sleep(millTime);
		} catch (Exception e) {

		}
	}
}
