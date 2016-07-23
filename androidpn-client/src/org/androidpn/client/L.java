package org.androidpn.client;

import android.util.Log;

/**
 * Log控制类
 * 
 * @author lijian
 * @date 2016-7-23 上午9:46:32
 */
public class L {
	private static final boolean flag = true;

	public static void i(String tag, String msg) {
		if (flag)
			Log.i("Androidpn_" + tag, msg);
	}

	public static void d(String tag, String msg) {
		if (flag)
			Log.d("Androidpn_" + tag, msg);
	}

	public static void e(String tag, String msg) {
		if (flag)
			Log.e("Androidpn_" + tag, msg);
	}

	public static void e(String tag, String msg, Throwable tr) {
		if (flag)
			Log.e("Androidpn_" + tag, msg, tr);
	}

	public static void v(String tag, String msg) {
		if (flag)
			Log.v("Androidpn_" + tag, msg);
	}

	public static void m(String tag, String msg) {
		if (flag)
			Log.e("Androidpn_" + tag, msg);
	}

	public static void w(String tag, String msg) {
		if (flag)
			Log.w("Androidpn_" + tag, msg);
	}
}