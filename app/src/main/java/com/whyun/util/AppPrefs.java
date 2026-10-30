package com.whyun.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.whyun.IBlueToothConst;
import com.whyun.activity.IMyPreference;

/**
 * 读写应用偏好。文件与旧版 PreferenceManager.getDefaultSharedPreferences 相同，已有数据可直接读取。
 */
public final class AppPrefs implements IMyPreference {

	private AppPrefs() {
	}

	public static SharedPreferences of(Context context) {
		Context app = context.getApplicationContext();
		return app.getSharedPreferences(app.getPackageName() + "_preferences", Context.MODE_PRIVATE);
	}

	/** 默认连接方式。旧版本存为字符串 "0"/"1" */
	public static int connectType(Context context) {
		String v = of(context).getString(CONNECT_TYPE, String.valueOf(IBlueToothConst.WIFI_TYPE));
		try {
			return Integer.parseInt(v);
		} catch (NumberFormatException e) {
			return IBlueToothConst.WIFI_TYPE;
		}
	}

	public static void setConnectType(Context context, int type) {
		of(context).edit().putString(CONNECT_TYPE, String.valueOf(type)).apply();
	}

	public static int wifiPort(Context context) {
		return of(context).getInt(WIFI_PORT, IBlueToothConst.PORT);
	}

	public static boolean get(Context context, String key, boolean def) {
		return of(context).getBoolean(key, def);
	}

	public static void put(Context context, String key, boolean value) {
		of(context).edit().putBoolean(key, value).apply();
	}

	public static int get(Context context, String key, int def) {
		return of(context).getInt(key, def);
	}

	public static void put(Context context, String key, int value) {
		of(context).edit().putInt(key, value).apply();
	}

	public static String get(Context context, String key, String def) {
		return of(context).getString(key, def);
	}

	public static void put(Context context, String key, String value) {
		of(context).edit().putString(key, value).apply();
	}
}
