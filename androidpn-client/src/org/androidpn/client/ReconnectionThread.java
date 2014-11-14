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
 * A thread class for recennecting the server.<br>
 * 重连线程(管理：当网络异常被中断后，线程内管理多少时间再次发起连接请求)
 * 
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public class ReconnectionThread extends Thread {

	private static final String LOGTAG = LogUtil
			.makeLogTag(ReconnectionThread.class);

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

	public void run() {
		try {
			while (!isInterrupted()) {// 如果线程没有被中断
				L.d(LOGTAG, "Trying to reconnect in " + waiting() + " seconds");
				Thread.sleep((long) waiting() * 1000L);// 睡眠几秒后，重新请求连接
				xmppManager.connect();
				waiting++;
			}
		} catch (final InterruptedException e) {
			xmppManager.getHandler().post(new Runnable() {
				public void run() {
					// 连接异常
					xmppManager.getConnectionListener().reconnectionFailed(e);
				}
			});
		}
	}

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
