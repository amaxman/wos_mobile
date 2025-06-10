/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package wos.mobile.entity;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;

import java.io.Serializable;
import java.util.List;

public class JsonMsg<T> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 是否成功
	 */
	private boolean msgType=true;

	/**
	 * 消息
	 */
	private String msg="";

	/**
	 * 数据
	 */
	private T data;

	/**
	 * 消息编码
	 */
	private int code=0;

	/**
	 * 实例化JsonMsg对象
	 * 
	 * @param t
	 * @return
	 */
	public static <T> JsonMsg<T> instancce(T t) {
		JsonMsg<T> jsonMsg = new JsonMsg<T>(t);
		return jsonMsg;
	}

	/**
	 * 实例化JsonMsg对象
	 * @param t 对象
	 * @param msg 消息
	 * @param msgType 消息类型
	 * @param <T> 泛型数据类型
	 * @return
	 */
	public static <T> JsonMsg<T> instance(T t,String msg,boolean msgType) {
		JsonMsg<T> jsonMsg = new JsonMsg<T>(t);
		jsonMsg.setMsg(msg);
		jsonMsg.setMsgType(msgType);
		return jsonMsg;
	}

	public static <T> JsonMsg<T> success(T t) {
		return instance(t,"",true);
	}

	public static <T> JsonMsg<T> success(T t,String msg) {
		return instance(t,msg,true);
	}

	public static <T> JsonMsg<T> error(String msg) {
		return instance(null,msg,false);
	}

	public JsonMsg() {
		code = 0;
		msgType = true;
	}

	public JsonMsg(T data) {
		code = 0;
		msgType = true;
		this.data = data;
	}

	/**
	 * 获取是否成功
	 * 
	 * @return 是否成功
	 */
	public boolean isMsgType() {
		return msgType;
	}

	/**
	 * 设定是否成功
	 * 
	 * @param msgType 是否成功
	 */
	public void setMsgType(boolean msgType) {
		this.msgType = msgType;
	}

	/**
	 * 获取消息
	 * 
	 * @return 消息
	 */
	public String getMsg() {
		return msg;
	}

	/**
	 * 设定消息
	 * 
	 * @param msg 消息
	 */
	public void setMsg(String msg) {
		this.msg = msg;
	}

	/**
	 * 获取数据
	 * 
	 * @return 数据
	 */
	public T getData() {
		return data;
	}

	/**
	 * 设定数据
	 * 
	 * @param data 数据
	 */
	public void setData(T data) {
		this.data = data;
	}

	/**
	 * 消息编码
	 * 
	 * @return
	 */
	public int getCode() {
		return code;
	}

	/**
	 * 消息编码
	 * 
	 * @param code 消息编码
	 */
	public void setCode(int code) {
		this.code = code;
	}

	/**
	 * 解析JSONString为JsonMsg
	 * @param json
	 * @param <T>
	 * @return
	 */
	public static <T> JsonMsg<T> parse(String json) {
		return JSONObject.parseObject(json,new TypeReference<JsonMsg<T>>() {});
	}

	/**
	 * 解析json列表
	 * @param json
	 * @param <T>
	 * @return
	 */
	public static <T> JsonMsg<List<T>> parseList(String json) {
		return JSONObject.parseObject(json, new TypeReference<JsonMsg<List<T>>>() {}, new Feature[0]);
	}


}
