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

/**
 * 重连线程(管理：当网络被异常中断后，线程内管理多少时间再次发起连接请求)<br>
 * 心跳维持（有一个心跳维持时间算法）
 * 
 * @author lijian
 * @date 2016-7-23 下午11:52:59
 */
public class ReconnectionThread extends Thread {
	private static final String TAG = "ReconnectionThread";

	private final XmppManager xmppManager;

	/** 等待时间(单位s) **/
	private int waiting;

	/**
	 * 重连线程(管理：当网络异常被中断后，线程内管理多少时间再次发起连接请求)
	 * 
	 * @param xmppManager
	 */
	ReconnectionThread(XmppManager xmppManager) {
		this.xmppManager = xmppManager;
		this.waiting = 0;
	}

	@Override
	public void run() {
		try {
			while (!isInterrupted()) {// 如果线程没有被中断
				L.i(TAG, waiting() + "s后，尝试重新连接");

				Thread.sleep((long) waiting() * 1000L);// 睡眠几秒后，重新请求连接
				xmppManager.connect();
				waiting++;
			}
		} catch (final InterruptedException e) {
			xmppManager.getHandler().post(new Runnable() {
				public void run() {
					// 连接异常（注册重连失败）
					xmppManager.getConnectionListener().reconnectionFailed(e);
				}
			});
		}
	}

	/**
	 * 等待重连时间算法
	 * 
	 * @return
	 */
	private int waiting() {
		if (waiting > 20) {
			return 600;
		}
		if (waiting > 13) {
			return 300;
		}
		return waiting <= 7 ? 10 : 60;
	}
}
