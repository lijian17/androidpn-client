/*
 * Copyright (C) 2010 Moduad Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.androidpn.client;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * A broadcast receiver to handle the changes in network connectiion states.<br>
 * 网络变化广播接收器
 * 
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public class ConnectivityReceiver extends BroadcastReceiver {

	private static final String LOGTAG = LogUtil
			.makeLogTag(ConnectivityReceiver.class);

	private NotificationService notificationService;

	public ConnectivityReceiver(NotificationService notificationService) {
		this.notificationService = notificationService;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		L.d(LOGTAG, "ConnectivityReceiver.onReceive()...");
		String action = intent.getAction();
		L.d(LOGTAG, "action=" + action);

		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		// 获取代表联网状态的NetWorkInfo对象
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

		// 获取当前的网络连接是否可用
		if (networkInfo != null) {
			L.d(LOGTAG, "Network Type  = " + networkInfo.getTypeName());
			L.d(LOGTAG, "Network State = " + networkInfo.getState());
			if (networkInfo.isConnected()) {
				L.i(LOGTAG, "Network connected");
				notificationService.connect();
			}
		} else {
			// 当网络不可用时，跳转到网络设置页面
			// startActivityForResult(new
			// Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS), 1);
			L.e(LOGTAG, "Network unavailable");
			notificationService.disconnect();
		}

//		ConnectivityManager mConnectivityManager = (ConnectivityManager) context
//				.getSystemService(Context.CONNECTIVITY_SERVICE);
//
//		// 获取代表联网状态的NetWorkInfo对象
//		NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
//		if (mNetworkInfo == null) {
//			Toast.makeText(context, "当前网络连接不可用", Toast.LENGTH_LONG).show();
//			// 当网络不可用时，跳转到网络设置页面
//			startActivityForResult(new Intent(
//					android.provider.Settings.ACTION_WIRELESS_SETTINGS), 1);
//		} else {
//			boolean available = networkInfo.isAvailable();
//			if (available) {
//				L.i("通知", "当前的网络连接可用");
//				Toast.makeText(context, "当前的网络连接可用", Toast.LENGTH_SHORT).show();
//			} else {
//				L.i("通知", "当前的网络连接不可用");
//				Toast.makeText(context, "当前的网络连接不可用", Toast.LENGTH_SHORT)
//						.show();
//			}
//		}
//		State state = mConnectivityManager.getNetworkInfo(
//				ConnectivityManager.TYPE_MOBILE).getState();
//		if (State.CONNECTED == state) {
//			L.i("通知", "GPRS网络已连接");
//			Toast.makeText(context, "GPRS网络已连接", Toast.LENGTH_SHORT).show();
//		}
//
//		state = mConnectivityManager.getNetworkInfo(
//				ConnectivityManager.TYPE_WIFI).getState();
//		if (State.CONNECTED == state) {
//			L.i("通知", "WIFI网络已连接");
//			Toast.makeText(context, "WIFI网络已连接", Toast.LENGTH_SHORT).show();
//		}
//
//		// // 跳转到无线网络设置界面
//		// startActivity(new
//		// Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
//		// // 跳转到无限wifi网络设置界面
//		// startActivity(new
//		// Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
	}

}
