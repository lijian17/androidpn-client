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

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Packet;

import android.content.Intent;

/**
 * 接收从服务器端推送过来的Packet监听器
 * 
 * @author lijian
 * @date 2016-7-24 上午6:49:06
 */
public class NotificationPacketListener implements PacketListener {
	private static final String TAG = "NotificationPacketListener";

	private final XmppManager xmppManager;

	/**
	 * 通知数据包监听器
	 * 
	 * @param xmppManager
	 */
	public NotificationPacketListener(XmppManager xmppManager) {
		this.xmppManager = xmppManager;
	}

	/**
	 * 处理数据包
	 */
	@Override
	public void processPacket(Packet packet) {
		L.i(TAG, "NotificationPacketListener.processPacket()...");
		L.i(TAG, "packet.toXML()=" + packet.toXML());

		if (packet instanceof NotificationIQ) {
			NotificationIQ notification = (NotificationIQ) packet;

			if (notification.getChildElementXML().contains(
					Constants.XMPP_PROTOCOL_NAMESPACE)) {
				String notificationId = notification.getId();
				String notificationApiKey = notification.getApiKey();
				String notificationTitle = notification.getTitle();
				String notificationMessage = notification.getMessage();
				// String notificationTicker = notification.getTicker();
				String notificationUri = notification.getUri();

				Intent intent = new Intent(Constants.ACTION_SHOW_NOTIFICATION);
				intent.putExtra(Constants.NOTIFICATION_ID, notificationId);
				intent.putExtra(Constants.NOTIFICATION_API_KEY, notificationApiKey);
				intent.putExtra(Constants.NOTIFICATION_TITLE, notificationTitle);
				intent.putExtra(Constants.NOTIFICATION_MESSAGE, notificationMessage);
				intent.putExtra(Constants.NOTIFICATION_URI, notificationUri);
				// intent.setData(Uri.parse((new StringBuilder(
				// "notif://notification.androidpn.org/")).append(
				// notificationApiKey).append("/").append(
				// System.currentTimeMillis()).toString()));

				xmppManager.getContext().sendBroadcast(intent);
			}
		}

	}

}
