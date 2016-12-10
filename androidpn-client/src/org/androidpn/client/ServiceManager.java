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

import java.util.Properties;

import org.jivesoftware.smack.packet.IQ;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.SystemClock;
import android.text.TextUtils;

/**
 * 加载客户端中的配置信息，并管理NotifactionService服务的启动与关闭
 * 
 * @author lijian
 * @date 2016-7-23 上午9:37:15
 */
public final class ServiceManager {
	private static final String TAG = "ServiceManager";

	private Context context;
	/** 偏好设定 **/
	private SharedPreferences sharedPrefs;

	/** 加载配置文件 **/
	private Properties props;
	/** 版本号 **/
	private String version = "0.5.0";
	/** api密钥 **/
	private String apiKey;
	/** xmpp地址 **/
	private String xmppHost;
	/** xmpp端口 **/
	private String xmppPort;

	/** 回调Activity的包名 **/
	private String callbackActivityPackageName;

	/** 回调Activity的全类名 **/
	private String callbackActivityClassName;

	public ServiceManager(Context context) {
		this.context = context;

		if (context instanceof Activity) {
			L.i(TAG, "Callback Activity...");
			Activity callbackActivity = (Activity) context;
			callbackActivityPackageName = callbackActivity.getPackageName();
			callbackActivityClassName = callbackActivity.getClass().getName();
		}

		props = loadProperties();
		apiKey = props.getProperty("apiKey", "");
		xmppHost = props.getProperty("xmppHost", "127.0.0.1");
		xmppPort = props.getProperty("xmppPort", "5222");
		L.i(TAG, "apiKey=" + apiKey);
		L.i(TAG, "xmppHost=" + xmppHost);
		L.i(TAG, "xmppPort=" + xmppPort);

		sharedPrefs = context.getSharedPreferences(
				Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
		Editor editor = sharedPrefs.edit();
		editor.putString(Constants.API_KEY, apiKey);
		editor.putString(Constants.VERSION, version);
		editor.putString(Constants.XMPP_HOST, xmppHost);
		editor.putInt(Constants.XMPP_PORT, Integer.parseInt(xmppPort));
		editor.putString(Constants.CALLBACK_ACTIVITY_PACKAGE_NAME,
				callbackActivityPackageName);
		editor.putString(Constants.CALLBACK_ACTIVITY_CLASS_NAME,
				callbackActivityClassName);
		editor.commit();
	}

	/**
	 * 开新线程启动服务
	 */
	public void startService() {
		Thread serviceThread = new Thread(new Runnable() {
			@Override
			public void run() {
				// Implicit intents with startService are not safe错误的解决方式
				// Android 5.0 service服务必须采用显示方式启动
				// 解决方案一：设置Action和packageName
				Intent intent = NotificationService.getIntent();
				// intent.setAction("org.androidpn.client.NotificationService");//
				// 你定义的service的action
				intent.setPackage(context.getPackageName());// 这里你需要设置你应用的包名
				context.startService(intent);
			}
		});
		serviceThread.start();
	}

	/**
	 * 停止服务
	 */
	public void stopService() {
		Intent intent = NotificationService.getIntent();
		context.stopService(intent);
	}

	/**
	 * 设置别名
	 * 
	 * @param alias
	 *            别名
	 */
	public void setAlias(final String alias) {
		final String username = sharedPrefs.getString(Constants.XMPP_USERNAME,
				"");
		if (TextUtils.isEmpty(alias) || TextUtils.isEmpty(username)) {
			return;
		}
		new Thread(new Runnable() {

			@Override
			public void run() {
				// 睡眠1s以保证NotificationService对象创建好
				SystemClock.sleep(1000);
				NotificationService notificationService = NotificationService
						.getNotificationService();
				XmppManager xmppManager = notificationService.getXmppManager();
				if (xmppManager != null) {
					if (!xmppManager.isAuthenticated()) {
						try {
							synchronized (xmppManager) {
								L.d(TAG, "等待身份认证");
								xmppManager.wait();
							}
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					L.d(TAG, "身份认证成功，现在开始发送“设置别名”");
					SetAliasIQ iq = new SetAliasIQ();
					iq.setType(IQ.Type.SET);
					iq.setUsername(username);
					iq.setAlias(alias);
					xmppManager.getConnection().sendPacket(iq);
				}
			}
		}).start();
	}

	/**
	 * 加载Properties配置文件
	 * 
	 * @return
	 */
	private Properties loadProperties() {
		Properties props = new Properties();
		try {
			int id = context.getResources().getIdentifier("androidpn", "raw",
					context.getPackageName());
			props.load(context.getResources().openRawResource(id));
		} catch (Exception e) {
			L.e(TAG, "找不到properties文件.", e);
		}
		return props;
	}

	/**
	 * 获取版本号
	 * 
	 * @return
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * 设置通知logo
	 * 
	 * @param iconId
	 */
	public void setNotificationIcon(int iconId) {
		Editor editor = sharedPrefs.edit();
		editor.putInt(Constants.NOTIFICATION_ICON, iconId);
		editor.commit();
	}

	/**
	 * 进入到通知设置界面
	 * 
	 * @param context
	 */
	public static void viewNotificationSettings(Context context) {
		Intent intent = new Intent(context, NotificationSettingsActivity.class);
		context.startActivity(intent);
	}
}
