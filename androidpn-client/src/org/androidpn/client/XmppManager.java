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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Future;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Registration;
import org.jivesoftware.smack.provider.ProviderManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;

/**
 * 管理客户端和服务器之间的连接，向服务器发送连接、注册、登陆请求任务
 * 
 * @author lijian
 * @date 2016-7-23 下午11:10:02
 */
public class XmppManager {
	private static final String TAG = "XmppManager";

	/** XMPP资源名称 **/
	private static final String XMPP_RESOURCE_NAME = "AndroidpnClient";

	/** 上下文 **/
	private Context context;

	/** 任务提交器 **/
	private NotificationService.TaskSubmitter taskSubmitter;

	/** 任务数量追踪器 **/
	private NotificationService.TaskTracker taskTracker;

	/** 获得SharedPreferences **/
	private SharedPreferences sharedPrefs;

	/** XMPP的IP **/
	private String xmppHost;

	/** XMPP的端口 **/
	private int xmppPort;

	/** 一个xmpp连接 **/
	private XMPPConnection connection;

	/** XMPP的用户名 **/
	private String username;

	/** XMPP的密码 **/
	private String password;

	/** 长连接监听器 **/
	private ConnectionListener connectionListener;

	/** 通知数据包监听器 **/
	private PacketListener notificationPacketListener;

	private Handler handler;

	/** 任务数组 **/
	private List<Runnable> taskList;

	/** 当前是否有任务正运行 **/
	private boolean running = false;

	/** 任务运行的将来结果 **/
	private Future<?> futureTask;

	/** 重连线程(管理：当网络异常被中断后，线程内管理多少时间再次发起连接请求) **/
	private Thread reconnection;

	public XmppManager(NotificationService notificationService) {
		context = notificationService;
		taskSubmitter = notificationService.getTaskSubmitter();
		taskTracker = notificationService.getTaskTracker();
		sharedPrefs = notificationService.getSharedPreferences();

		xmppHost = sharedPrefs.getString(Constants.XMPP_HOST, "localhost");
		xmppPort = sharedPrefs.getInt(Constants.XMPP_PORT, 5222);
		username = sharedPrefs.getString(Constants.XMPP_USERNAME, "");
		password = sharedPrefs.getString(Constants.XMPP_PASSWORD, "");

		connectionListener = new PersistentConnectionListener(this);
		notificationPacketListener = new NotificationPacketListener(this);

		handler = new Handler();
		taskList = new ArrayList<Runnable>();
		reconnection = new ReconnectionThread(this);
	}

	public Context getContext() {
		return context;
	}

	/**
	 * 建立连接->提交登录任务->提交注册任务->提交连接任务
	 */
	public void connect() {
		L.i(TAG, "connect()...");
		submitLoginTask();
	}

	/**
	 * 断开连接
	 */
	public void disconnect() {
		L.i(TAG, "disconnect()...");
		terminatePersistentConnection();
	}

	/**
	 * 终止长连接
	 */
	public void terminatePersistentConnection() {
		L.i(TAG, "terminatePersistentConnection()...");
		Runnable runnable = new Runnable() {

			final XmppManager xmppManager = XmppManager.this;

			public void run() {
				if (xmppManager.isConnected()) {
					L.i(TAG, "terminatePersistentConnection()... run()");
					xmppManager.getConnection().removePacketListener(
							xmppManager.getNotificationPacketListener());
					xmppManager.getConnection().disconnect();
				}
				xmppManager.runTask();
			}

		};
		addTask(runnable);
	}

	/**
	 * 得到xmpp连接
	 * 
	 * @return
	 */
	public XMPPConnection getConnection() {
		return connection;
	}

	/**
	 * 设置连接
	 * 
	 * @param connection
	 *            一个xmpp连接
	 */
	public void setConnection(XMPPConnection connection) {
		this.connection = connection;
	}

	/**
	 * 得到xmpp的用户名
	 * 
	 * @return
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * 设置xmpp的用户名
	 * 
	 * @param username
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * 得到xmpp的密码
	 * 
	 * @return
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * 设置xmpp的密码
	 * 
	 * @param password
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * 得到长连接监听器
	 * 
	 * @return
	 */
	public ConnectionListener getConnectionListener() {
		return connectionListener;
	}

	/**
	 * 得到通知数据包监听器
	 * 
	 * @return
	 */
	public PacketListener getNotificationPacketListener() {
		return notificationPacketListener;
	}

	/**
	 * 开启重连线程
	 */
	public void startReconnectionThread() {
		synchronized (reconnection) {
			if (!reconnection.isAlive()) {// 如果已经不是存活的,那将线程重启
				reconnection.setName("Xmpp Reconnection Thread");
				reconnection.start();
			}
		}
	}

	public Handler getHandler() {
		return handler;
	}

	/**
	 * 重新注册账户
	 */
	public void reregisterAccount() {
		removeAccount();
		submitLoginTask();
		runTask();
	}

	/**
	 * 得到任务列表
	 * 
	 * @return
	 */
	public List<Runnable> getTaskList() {
		return taskList;
	}

	/**
	 * 得到任务将来结果
	 * 
	 * @return
	 */
	public Future<?> getFutureTask() {
		return futureTask;
	}

	/**
	 * 运行任务
	 */
	public void runTask() {
		L.i(TAG, "runTask()...start");
		synchronized (taskList) {
			running = false;
			futureTask = null;
			if (!taskList.isEmpty()) {
				// 从任务队列获取一个任务并执行
				Runnable runnable = (Runnable) taskList.get(0);
				taskList.remove(0);
				running = true;
				futureTask = taskSubmitter.submit(runnable);
				if (futureTask == null) {
					taskTracker.decrease();
				}
			}
		}
		taskTracker.decrease();
		L.i(TAG, "runTask()...end");
	}

	/**
	 * 得到一个随机生成的UUID
	 * 
	 * @return
	 */
	private String newRandomUUID() {
		String uuidRaw = UUID.randomUUID().toString();
		L.i(TAG, "newRandomUUID--->" + uuidRaw);
		return uuidRaw.replaceAll("-", "");
	}

	/**
	 * xmpp连接是否建立并连接成功
	 * 
	 * @return
	 */
	private boolean isConnected() {
		return connection != null && connection.isConnected();
	}

	/**
	 * 建立连接成功的账户是否被认证
	 * 
	 * @return
	 */
	private boolean isAuthenticated() {
		return connection != null && connection.isConnected()
				&& connection.isAuthenticated();
	}

	/**
	 * 是否是已注册的xmpp用户
	 * 
	 * @return
	 */
	private boolean isRegistered() {
		return sharedPrefs.contains(Constants.XMPP_USERNAME)
				&& sharedPrefs.contains(Constants.XMPP_PASSWORD);
	}

	/**
	 * 提交连接任务
	 */
	private void submitConnectTask() {
		L.i(TAG, "submitConnectTask()...");
		addTask(new ConnectTask());
	}

	/**
	 * 提交注册任务
	 */
	private void submitRegisterTask() {
		L.i(TAG, "submitRegisterTask()...");
		submitConnectTask();
		addTask(new RegisterTask());
	}

	/**
	 * 提交登录任务
	 */
	private void submitLoginTask() {
		L.i(TAG, "submitLoginTask()...");
		submitRegisterTask();
		addTask(new LoginTask());
	}

	/**
	 * 添加任务到线程池
	 * 
	 * @param runnable
	 */
	private void addTask(Runnable runnable) {
		L.i(TAG, "addTask(runnable)...start");
		taskTracker.increase();
		synchronized (taskList) {
			if (taskList.isEmpty() && !running) {// 如果任务列表为空,并且没有任务在运行中
				running = true;
				futureTask = taskSubmitter.submit(runnable);
				if (futureTask == null) {// 如果得到的将来的结果为null
					taskTracker.decrease();
				}
			} else {
				// 否则加入任务队列中
				taskList.add(runnable);
			}
		}
		L.i(TAG, "addTask(runnable)... end");
	}

	/**
	 * 删除账户
	 */
	private void removeAccount() {
		Editor editor = sharedPrefs.edit();
		editor.remove(Constants.XMPP_USERNAME);
		editor.remove(Constants.XMPP_PASSWORD);
		editor.commit();
	}

	/**
	 * 创建一个线程-->连接到服务器的
	 */
	private class ConnectTask implements Runnable {

		final XmppManager xmppManager;

		private ConnectTask() {
			this.xmppManager = XmppManager.this;
		}

		public void run() {
			L.i(TAG, "ConnectTask.run()...");

			if (!xmppManager.isConnected()) {
				// 给这个新的xmpp连接创建一个配置信息
				ConnectionConfiguration connConfig = new ConnectionConfiguration(
						xmppHost, xmppPort);
				// connConfig.setSecurityMode(SecurityMode.disabled);// 禁用安全模式
				connConfig.setSecurityMode(SecurityMode.required);// 设置安全模式
				connConfig.setSASLAuthenticationEnabled(false);// 设置SASL认证是否启用
				connConfig.setCompressionEnabled(false);// 设置数据压缩是否启用

				// 新建一个xmpp连接
				XMPPConnection connection = new XMPPConnection(connConfig);
				xmppManager.setConnection(connection);

				try {
					// 连接到服务器
					connection.connect();
					L.i(TAG, "XMPP连接服务器成功");

					// 添加一个数据包(IQ)提供者
					ProviderManager.getInstance().addIQProvider(
							Constants.XMPP_PROTOCOL_ELEMENTNAME,
							Constants.XMPP_PROTOCOL_NAMESPACE,
							new NotificationIQProvider());

				} catch (XMPPException e) {
					L.e(TAG, "XMPP连接失败", e);
				}

				xmppManager.runTask();

			} else {
				// 如果XMPP连接之前已经建立,那么就运行这个任务
				L.i(TAG, "XMPP已经是连接状态的");
				xmppManager.runTask();
			}
		}
	}

	/**
	 * 创建一个线程-->注册一个新用户到服务器的
	 */
	private class RegisterTask implements Runnable {

		final XmppManager xmppManager;

		private RegisterTask() {
			xmppManager = XmppManager.this;
		}

		public void run() {
			L.i(TAG, "RegisterTask.run()...");

			if (!xmppManager.isRegistered()) {
				final String newUsername = newRandomUUID();
				final String newPassword = newRandomUUID();

				// 创建一个限量注册
				Registration registration = new Registration();

				// 数据包过滤器
				PacketFilter packetFilter = new AndFilter(new PacketIDFilter(
						registration.getPacketID()), new PacketTypeFilter(
						IQ.class));

				// 数据包监听
				PacketListener packetListener = new PacketListener() {

					/**
					 * 数据包过程
					 */
					@Override
					public void processPacket(Packet packet) {
						L.i("RegisterTask.PacketListener",
								"processPacket().....");
						L.i("RegisterTask.PacketListener",
								"packet=" + packet.toXML());

						if (packet instanceof IQ) {// 如果是iq,强转为IQ
							IQ response = (IQ) packet;
							if (response.getType() == IQ.Type.ERROR) {// 如果是一个错误消息数据包
								if (!response.getError().toString()
										.contains("409")) {
									L.e(TAG, "注册XMPP帐户时未知错误！ "
											+ response.getError()
													.getCondition());
								}
							} else if (response.getType() == IQ.Type.RESULT) {
								xmppManager.setUsername(newUsername);
								xmppManager.setPassword(newPassword);
								L.i(TAG, "username=" + newUsername);
								L.i(TAG, "password=" + newPassword);

								Editor editor = sharedPrefs.edit();
								editor.putString(Constants.XMPP_USERNAME,
										newUsername);
								editor.putString(Constants.XMPP_PASSWORD,
										newPassword);
								editor.commit();
								L.i(TAG, "帐户注册成功");
								xmppManager.runTask();
							}
						}
					}
				};

				// 给XMPP连接-->添加数据包监听器
				connection.addPacketListener(packetListener, packetFilter);

				registration.setType(IQ.Type.SET);
				// registration.setTo(xmppHost);
				// Map<String, String> attributes = new HashMap<String,
				// String>();
				// attributes.put("username", rUsername);
				// attributes.put("password", rPassword);
				// registration.setAttributes(attributes);
				registration.addAttribute("username", newUsername);
				registration.addAttribute("password", newPassword);
				connection.sendPacket(registration);// 发送注册数据包

			} else {
				L.i(TAG, "帐户是已经注册的");
				xmppManager.runTask();
			}
		}
	}

	/**
	 * 创建一个线程-->登陆到服务器的
	 */
	private class LoginTask implements Runnable {

		final XmppManager xmppManager;

		private LoginTask() {
			this.xmppManager = XmppManager.this;
		}

		public void run() {
			L.i(TAG, "LoginTask.run()...");

			if (!xmppManager.isAuthenticated()) {// 如果账户未认证
				L.i(TAG, "username=" + username);
				L.i(TAG, "password=" + password);

				try {
					// 登陆
					xmppManager.getConnection().login(
							xmppManager.getUsername(),
							xmppManager.getPassword(), XMPP_RESOURCE_NAME);
					L.i(TAG, "Loggedn in successfully");

					// 注册长连接监听器
					if (xmppManager.getConnectionListener() != null) {
						xmppManager.getConnection().addConnectionListener(
								xmppManager.getConnectionListener());
					}

					// 数据包过滤器
					PacketFilter packetFilter = new PacketTypeFilter(
							NotificationIQ.class);
					// 注册数据包监听器
					PacketListener packetListener = xmppManager
							.getNotificationPacketListener();
					connection.addPacketListener(packetListener, packetFilter);

					xmppManager.runTask();

				} catch (XMPPException e) {
					L.e(TAG, "LoginTask.run()... xmpp登陆error");
					L.e(TAG, "无法登录到XMPP服务器. Caused by: " + e.getMessage());
					String INVALID_CREDENTIALS_ERROR_CODE = "401";
					String errorMessage = e.getMessage();
					// 如果登陆失败,且错误码为401,那么就发起重新注册账户
					if (errorMessage != null
							&& errorMessage
									.contains(INVALID_CREDENTIALS_ERROR_CODE)) {
						xmppManager.reregisterAccount();
						return;
					}
					// 开启重连线程
					xmppManager.startReconnectionThread();

				} catch (Exception e) {
					L.e(TAG, "LoginTask.run()... other error");
					L.e(TAG, "无法登录到XMPP服务器. Caused by: " + e.getMessage());
					xmppManager.startReconnectionThread();
				}

			} else {
				L.i(TAG, "账户是已经登陆的");
				xmppManager.runTask();
			}

		}
	}

}
