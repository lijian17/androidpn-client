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

import java.util.Random;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

/**
 * 创建状态栏通知
 * 
 * @author lijian
 * @date 2016-7-23 下午12:48:16
 */
public class Notifier {
	private static final String TAG = "Notifier";

	private static final Random random = new Random(System.currentTimeMillis());

	private Context context;

	private SharedPreferences sharedPrefs;

	private NotificationManager notificationManager;

	public Notifier(Context context) {
		this.context = context;
		this.sharedPrefs = context.getSharedPreferences(
				Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
		this.notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
	}

	public void notify(String notificationId, String apiKey, String title,
			String message, String uri) {
		L.i(TAG, "notify()...");

		L.i(TAG, "notificationId=" + notificationId);
		L.i(TAG, "notificationApiKey=" + apiKey);
		L.i(TAG, "notificationTitle=" + title);
		L.i(TAG, "notificationMessage=" + message);
		L.i(TAG, "notificationUri=" + uri);

		if (isNotificationEnabled()) {
			// 显示Toast
			if (isNotificationToastEnabled()) {
				Toast.makeText(context, message, Toast.LENGTH_LONG).show();
			}

			// Notification
			Notification notification = new Notification();
			notification.icon = getNotificationIcon();
			notification.defaults = Notification.DEFAULT_LIGHTS;
			if (isNotificationSoundEnabled()) {
				notification.defaults |= Notification.DEFAULT_SOUND;
			}
			if (isNotificationVibrateEnabled()) {
				notification.defaults |= Notification.DEFAULT_VIBRATE;
			}
			notification.flags |= Notification.FLAG_AUTO_CANCEL;
			notification.when = System.currentTimeMillis();
			notification.tickerText = message;

			// Intent intent;
			// if (uri != null
			// && uri.length() > 0
			// && (uri.startsWith("http:") || uri.startsWith("https:")
			// || uri.startsWith("tel:") || uri.startsWith("geo:"))) {
			// intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
			// } else {
			// String callbackActivityPackageName = sharedPrefs.getString(
			// Constants.CALLBACK_ACTIVITY_PACKAGE_NAME, "");
			// String callbackActivityClassName = sharedPrefs.getString(
			// Constants.CALLBACK_ACTIVITY_CLASS_NAME, "");
			// intent = new Intent().setClassName(callbackActivityPackageName,
			// callbackActivityClassName);
			// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			// intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			// }

			Intent intent = new Intent(context,
					NotificationDetailsActivity.class);
			intent.putExtra(Constants.NOTIFICATION_ID, notificationId);
			intent.putExtra(Constants.NOTIFICATION_API_KEY, apiKey);
			intent.putExtra(Constants.NOTIFICATION_TITLE, title);
			intent.putExtra(Constants.NOTIFICATION_MESSAGE, message);
			intent.putExtra(Constants.NOTIFICATION_URI, uri);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
			intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

			PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
					intent, PendingIntent.FLAG_UPDATE_CURRENT);

			notification.setLatestEventInfo(context, title, message,
					contentIntent);
			notificationManager.notify(random.nextInt(), notification);

			// Intent clickIntent = new Intent(
			// Constants.ACTION_NOTIFICATION_CLICKED);
			// clickIntent.putExtra(Constants.NOTIFICATION_ID, notificationId);
			// clickIntent.putExtra(Constants.NOTIFICATION_API_KEY, apiKey);
			// clickIntent.putExtra(Constants.NOTIFICATION_TITLE, title);
			// clickIntent.putExtra(Constants.NOTIFICATION_MESSAGE, message);
			// clickIntent.putExtra(Constants.NOTIFICATION_URI, uri);
			// // positiveIntent.setData(Uri.parse((new StringBuilder(
			// // "notif://notification.adroidpn.org/")).append(apiKey).append(
			// // "/").append(System.currentTimeMillis()).toString()));
			// PendingIntent clickPendingIntent = PendingIntent.getBroadcast(
			// context, 0, clickIntent, 0);
			//
			// notification.setLatestEventInfo(context, title, message,
			// clickPendingIntent);
			//
			// Intent clearIntent = new Intent(
			// Constants.ACTION_NOTIFICATION_CLEARED);
			// clearIntent.putExtra(Constants.NOTIFICATION_ID, notificationId);
			// clearIntent.putExtra(Constants.NOTIFICATION_API_KEY, apiKey);
			// // negativeIntent.setData(Uri.parse((new StringBuilder(
			// // "notif://notification.adroidpn.org/")).append(apiKey).append(
			// // "/").append(System.currentTimeMillis()).toString()));
			// PendingIntent clearPendingIntent = PendingIntent.getBroadcast(
			// context, 0, clearIntent, 0);
			// notification.deleteIntent = clearPendingIntent;
			//
			// notificationManager.notify(random.nextInt(), notification);

		} else {
			L.w(TAG, "Notificaitons disabled.");
		}
	}

	/**
	 * 得到通知的logo
	 * 
	 * @return
	 */
	private int getNotificationIcon() {
		return sharedPrefs.getInt(Constants.NOTIFICATION_ICON, 0);
	}

	/**
	 * 是否显示推送的通知
	 * 
	 * @return
	 */
	private boolean isNotificationEnabled() {
		return sharedPrefs.getBoolean(Constants.SETTINGS_NOTIFICATION_ENABLED,
				true);
	}

	/**
	 * 当接到推送通知-->是否播放通知声音
	 * 
	 * @return
	 */
	private boolean isNotificationSoundEnabled() {
		return sharedPrefs.getBoolean(Constants.SETTINGS_SOUND_ENABLED, true);
	}

	/**
	 * 当接到推送通知-->是否震动手机
	 * 
	 * @return
	 */
	private boolean isNotificationVibrateEnabled() {
		return sharedPrefs.getBoolean(Constants.SETTINGS_VIBRATE_ENABLED, true);
	}

	/**
	 * 当接到推送通知-->是否显示吐司
	 * 
	 * @return
	 */
	private boolean isNotificationToastEnabled() {
		return sharedPrefs.getBoolean(Constants.SETTINGS_TOAST_ENABLED, false);
	}

}
