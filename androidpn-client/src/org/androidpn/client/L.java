package org.androidpn.client;

import android.util.Log;

/**
 * Log控制类
 * 
 * @author lijian
 * @date 2016-7-23 上午9:46:32
 */
public class L {
	private static final String PREFIX = "Androidpn->";

	private static final boolean flag = true;

	public static void i(String tag, String msg) {
		if (flag)
			Log.i(PREFIX + tag, msg);
	}

	public static void d(String tag, String msg) {
		if (flag)
			Log.d(PREFIX + tag, msg);
	}

	public static void e(String tag, String msg) {
		if (flag)
			Log.e(PREFIX + tag, msg);
	}

	public static void e(String tag, String msg, Throwable tr) {
		if (flag)
			Log.e(PREFIX + tag, msg, tr);
	}

	public static void v(String tag, String msg) {
		if (flag)
			Log.v(PREFIX + tag, msg);
	}

	public static void m(String tag, String msg) {
		if (flag)
			Log.e(PREFIX + tag, msg);
	}

	public static void w(String tag, String msg) {
		if (flag)
			Log.w(PREFIX + tag, msg);
	}
}