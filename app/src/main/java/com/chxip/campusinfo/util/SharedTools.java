package com.chxip.campusinfo.util;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import org.apache.commons.codec.binary.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;


public class SharedTools {

	public static SharedPreferences sp;

	/**
	 * 登录的用户名
	 */
	public static String  UserName="UserName";
	/**
	 * 登录的密码
	 */
	public static String  Password="Password";

	/**
	 * 登录人员信息
	 */
	public static String User="User";

	/**
	 * 省
	 */
	public static String Province="Province";

	/**
	 * 城市
	 */
	public static String City="City";

	/**
	 * 区
	 */
	public static String District="District";

	/**
	 * 保存String类型的 数据
	 *
	 */
	public static void saveData(String key,String value){
		Editor editor=sp.edit();
		editor.putString(key, value);
		editor.commit();
	}

	/**
	 * 保存boolean类型的 数据
	 *
	 */
	public static void saveData(String key,boolean value){
		Editor editor=sp.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	/**
	 * 取出String类型的数据
	 */
	public static String getString(String key){
		if(key!=null){
			return sp.getString(key,"");
		}
		return "";
	}
	/**
	 * 取出boolean类型的数据 默认值为true
	 */
	public static boolean getBoolean(String key){
		return sp.getBoolean(key,true);
	}

	/**
	 * 取出boolean类型的数据 默认值为False
	 */
	public static boolean getBooleanFalse(String key){
		return sp.getBoolean(key,false);
	}
	/**
	 * 清除数据
	 */
	public static void deleteDate(String key){
		Editor editor=sp.edit();
		editor.remove(key);
		editor.commit();
	}



	/**
	 * 从SharedPreferences 取出对象数据
	 */
	public static Object readObject(String key) {
		Object object = null;
		String employeeBase64 = sp.getString(key, "");
		//读取字节
		byte[] base64 = Base64.decodeBase64(employeeBase64.getBytes());
		//封装到字节流
		ByteArrayInputStream bais = new ByteArrayInputStream(base64);
		try {
			//再次封装
			ObjectInputStream bis = new ObjectInputStream(bais);
			try {
				//读取对象
				object =  bis.readObject();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (StreamCorruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return object;
	}

	/**
	 * 使用SharedPreferences 保存对象数据
	 */
	public static void saveObject(Object object,String key) {
		//创建字节输出流
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			//创建对象输出流，并封装字节流
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			//将对象写入字节流
			oos.writeObject(object);
			//将字节流编码成base64的字符窜
			String employeeBase64 = new String(Base64.encodeBase64(baos
					.toByteArray()));

			Editor editor = sp.edit();
			editor.putString(key, employeeBase64);
			editor.commit();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	
}
