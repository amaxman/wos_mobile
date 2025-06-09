package wos.mobile.entity;

import android.content.Context;

import java.util.HashMap;


/*
 *	信息管理 
 */
public class Property {
	public static String PACKAGE_NAME = "wos.mobile"; // 包名

	// 用户信息
	public static String sessionId;
	public static String staffName;

	public static String compName;

	//程序
	public static Context context;

	// 手机屏幕大小
	public static int screenwidth = 0;
	public static int screenheight = 0;

	/**
	 * 配置信息
	 */
	public static HashMap<String,String> hmConfig=new HashMap<>();
}
