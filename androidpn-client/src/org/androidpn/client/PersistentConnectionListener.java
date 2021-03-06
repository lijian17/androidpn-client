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

import org.jivesoftware.smack.ConnectionListener;

/**
 * 监控客户端与服务器之间的连接关闭和重新连接事件(长连接监听器)
 * 
 * @author lijian
 * @date 2016-7-23 下午11:32:14
 */
public class PersistentConnectionListener implements ConnectionListener {
	private static final String TAG = "PersistentConnectionListener";

	private final XmppManager xmppManager;

	/**
	 * 长连接监听器
	 * 
	 * @param xmppManager
	 */
	public PersistentConnectionListener(XmppManager xmppManager) {
		this.xmppManager = xmppManager;
	}

	/**
	 * 连接正常断开
	 */
	@Override
	public void connectionClosed() {
		L.i(TAG, "connectionClosed()...");
	}

	/**
	 * 连接异常断开
	 */
	@Override
	public void connectionClosedOnError(Exception e) {
		L.i(TAG, "connectionClosedOnError()...");
		if (xmppManager.getConnection() != null
				&& xmppManager.getConnection().isConnected()) {// 如果连接不为空,且是连接状态的
			xmppManager.getConnection().disconnect();// 断开连接
		}
		xmppManager.startReconnectionThread();
	}

	/**
	 * 重新连接ing
	 */
	@Override
	public void reconnectingIn(int seconds) {
		L.i(TAG, "reconnectingIn()...");
	}

	/**
	 * 重新连接失败
	 */
	@Override
	public void reconnectionFailed(Exception e) {
		L.i(TAG, "reconnectionFailed()...");
	}

	/**
	 * 重新连接成功
	 */
	@Override
	public void reconnectionSuccessful() {
		L.i(TAG, "reconnectionSuccessful()...");
	}

}
