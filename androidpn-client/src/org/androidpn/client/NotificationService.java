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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

/**
 * Service that continues to run in background and respond to the push
 * notification events from the server. This should be registered as service in
 * AndroidManifest.xml. <br>
 * 后台运行并响应来自服务器的事件推送通知服务
 * 
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public class NotificationService extends Service {

	private static final String LOGTAG = LogUtil.makeLogTag(NotificationService.class);

	public static final String SERVICE_NAME = "org.androidpn.client.NotificationService";

	/** 手机管理器 **/
	private TelephonyManager telephonyManager;

	// private WifiManager wifiManager;
	//
	// private ConnectivityManager connectivityManager;

	/** 广播接收者(通知栏消息显示) **/
	private BroadcastReceiver notificationReceiver;

	/** 网络是否可用广播接收者 **/
	private BroadcastReceiver connectivityReceiver;

	/** 手机状态改变监听(网络数据) **/
	private PhoneStateListener phoneStateListener;

	/** 创建一个单线程池 **/
	private ExecutorService executorService;

	/** 提交一个新的运行任务 **/
	private TaskSubmitter taskSubmitter;

	/** 监测(控制)运行中的任务数 **/
	private TaskTracker taskTracker;

	/** xmpp管理器 **/
	private XmppManager xmppManager;

	/** SharedPreferences **/
	private SharedPreferences sharedPrefs;

	/** 设备ID **/
	private String deviceId;

	public NotificationService() {
		notificationReceiver = new NotificationReceiver();// 广播接收者
		connectivityReceiver = new ConnectivityReceiver(this);// 网络是否可用广播接收者
		phoneStateListener = new PhoneStateChangeListener(this);// 手机状态改变监听(网络数据)
		executorService = Executors.newSingleThreadExecutor();// 创建一个单线程池
		taskSubmitter = new TaskSubmitter(this);// 提交一个新的运行任务
		taskTracker = new TaskTracker(this);// 监测(控制)运行中的任务数
	}

	@Override
	public void onCreate() {
		L.d(LOGTAG, "onCreate()...");
		telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		// wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		// connectivityManager = (ConnectivityManager)
		// getSystemService(Context.CONNECTIVITY_SERVICE);

		sharedPrefs = getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);

		// Get deviceId
		deviceId = telephonyManager.getDeviceId();
		// L.d(LOGTAG, "deviceId=" + deviceId);
		Editor editor = sharedPrefs.edit();
		editor.putString(Constants.DEVICE_ID, deviceId);
		editor.commit();

		// 如果在模拟器上运行
		if (deviceId == null || deviceId.trim().length() == 0 || deviceId.matches("0+")) {
			if (sharedPrefs.contains("EMULATOR_DEVICE_ID")) {
				deviceId = sharedPrefs.getString(Constants.EMULATOR_DEVICE_ID, "");
			} else {
				// eg:deviceId=EMU8518114886176089842
				deviceId = (new StringBuilder("EMU")).append((new Random(System.currentTimeMillis())).nextLong()).toString();
				editor.putString(Constants.EMULATOR_DEVICE_ID, deviceId);
				editor.commit();
			}
		}
		L.d(LOGTAG, "deviceId=" + deviceId);

		xmppManager = new XmppManager(this);

		taskSubmitter.submit(new Runnable() {
			public void run() {
				NotificationService.this.start();
			}
		});
	}

	@Override
	public void onStart(Intent intent, int startId) {
		L.d(LOGTAG, "onStart()...");
	}

	@Override
	public void onDestroy() {
		L.d(LOGTAG, "onDestroy()...");
		stop();
	}

	@Override
	public IBinder onBind(Intent intent) {
		L.d(LOGTAG, "onBind()...");
		return null;
	}

	@Override
	public void onRebind(Intent intent) {
		L.d(LOGTAG, "onRebind()...");
	}

	@Override
	public boolean onUnbind(Intent intent) {
		L.d(LOGTAG, "onUnbind()...");
		return true;
	}

	/**
	 * 获得一个意图(org.androidpn.client.NotificationService)
	 * 
	 * @return
	 */
	public static Intent getIntent() {
		return new Intent(SERVICE_NAME);
	}

	/**
	 * 获得线程池
	 * 
	 * @return
	 */
	public ExecutorService getExecutorService() {
		return executorService;
	}

	/**
	 * 获得提交一个新的运行任务
	 * 
	 * @return
	 */
	public TaskSubmitter getTaskSubmitter() {
		return taskSubmitter;
	}

	/**
	 * 监测(控制)运行中的任务数
	 * 
	 * @return
	 */
	public TaskTracker getTaskTracker() {
		return taskTracker;
	}

	/**
	 * 获得xmpp管理器
	 * 
	 * @return
	 */
	public XmppManager getXmppManager() {
		return xmppManager;
	}

	/**
	 * 获得SharedPreferences
	 * 
	 * @return
	 */
	public SharedPreferences getSharedPreferences() {
		return sharedPrefs;
	}

	/**
	 * 获得设备ID
	 * 
	 * @return
	 */
	public String getDeviceId() {
		return deviceId;
	}

	/**
	 * 网络是连接的
	 */
	public void connect() {
		L.d(LOGTAG, "connect()...");
		taskSubmitter.submit(new Runnable() {
			public void run() {
				NotificationService.this.getXmppManager().connect();
			}
		});
	}

	/**
	 * 网络没有连接
	 */
	public void disconnect() {
		L.d(LOGTAG, "disconnect()...");
		taskSubmitter.submit(new Runnable() {
			public void run() {
				NotificationService.this.getXmppManager().disconnect();
			}
		});
	}

	/**
	 * 注册广播接收者(通知栏消息显示)
	 */
	private void registerNotificationReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.ACTION_SHOW_NOTIFICATION);
		filter.addAction(Constants.ACTION_NOTIFICATION_CLICKED);
		filter.addAction(Constants.ACTION_NOTIFICATION_CLEARED);
		registerReceiver(notificationReceiver, filter);
	}

	/**
	 * 注销广播接收者(通知栏消息显示)
	 */
	private void unregisterNotificationReceiver() {
		unregisterReceiver(notificationReceiver);
	}

	/**
	 * 注册(网络是否可用广播接收者 )
	 */
	private void registerConnectivityReceiver() {
		L.d(LOGTAG, "registerConnectivityReceiver()...");
		//监听数据连接状态
		telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_DATA_CONNECTION_STATE);
		IntentFilter filter = new IntentFilter();
		// filter.addAction(android.net.wifi.WifiManager.NETWORK_STATE_CHANGED_ACTION);
		filter.addAction(android.net.ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(connectivityReceiver, filter);
	}

	/**
	 * 注销(网络是否可用广播接收者 )
	 */
	private void unregisterConnectivityReceiver() {
		L.d(LOGTAG, "unregisterConnectivityReceiver()...");
		//停止监听
		telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
		unregisterReceiver(connectivityReceiver);
	}

	/**
	 * 注册广播接收者(通知栏、网络)
	 */
	private void start() {
		L.d(LOGTAG, "start()...");
		registerNotificationReceiver();
		registerConnectivityReceiver();
		// Intent intent = getIntent();
		// startService(intent);
		xmppManager.connect();
	}

	/**
	 * 注销广播接收者(通知栏、网络)
	 */
	private void stop() {
		L.d(LOGTAG, "stop()...");
		unregisterNotificationReceiver();
		unregisterConnectivityReceiver();
		xmppManager.disconnect();
		executorService.shutdown();
	}

	/**
	 * 提交一个新的运行任务
	 */
	public class TaskSubmitter {

		final NotificationService notificationService;

		public TaskSubmitter(NotificationService notificationService) {
			this.notificationService = notificationService;
		}

		@SuppressWarnings("unchecked")
		public Future submit(Runnable task) {
			Future result = null;
			if (!notificationService.getExecutorService().isTerminated() && !notificationService.getExecutorService().isShutdown() && task != null) {// 如果线程还在运行
				result = notificationService.getExecutorService().submit(task);//提交一个任务
			}
			return result;
		}

	}

	/**
	 * 监测(控制)运行中的任务数
	 */
	public class TaskTracker {

		final NotificationService notificationService;

		public int count;

		public TaskTracker(NotificationService notificationService) {
			this.notificationService = notificationService;
			this.count = 0;
		}

		public void increase() {
			synchronized (notificationService.getTaskTracker()) {
				notificationService.getTaskTracker().count++;
				L.d(LOGTAG, "Incremented task count to " + count);
			}
		}

		public void decrease() {
			synchronized (notificationService.getTaskTracker()) {
				notificationService.getTaskTracker().count--;
				L.d(LOGTAG, "Decremented task count to " + count);
			}
		}

	}

}
