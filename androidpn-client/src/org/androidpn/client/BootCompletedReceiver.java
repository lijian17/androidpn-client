package org.androidpn.client;

import org.androidpn.demoapp.R;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

/**
 * 开机广播
 * 
 * @author lijian
 * @date 2016-12-11 下午10:49:59
 */
public class BootCompletedReceiver extends BroadcastReceiver {
	private static final String TAG = "BootCompletedReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		L.i(TAG, "监听到开机广播");
		SharedPreferences pref = context.getSharedPreferences(
				Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
		if (pref.getBoolean(Constants.SETTINGS_AUTO_START, true)) {
			// 启动服务
			ServiceManager serviceManager = new ServiceManager(context);
			serviceManager.setNotificationIcon(R.drawable.notification);
			serviceManager.startService();
			L.i(TAG, "开机自启动推送通知服务");
		}
	}
}
