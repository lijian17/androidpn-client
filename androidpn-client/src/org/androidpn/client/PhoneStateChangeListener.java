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

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

/**
 * 监听手机状态
 * 
 * @author lijian
 * @date 2016-7-23 下午1:14:17
 */
public class PhoneStateChangeListener extends PhoneStateListener {
	private static final String TAG = "PhoneStateChangeListener";

	private final NotificationService notificationService;

	public PhoneStateChangeListener(NotificationService notificationService) {
		this.notificationService = notificationService;
	}

	@Override
	public void onDataConnectionStateChanged(int state) {
		super.onDataConnectionStateChanged(state);
		L.i(TAG, "onDataConnectionStateChanged()...");
		L.i(TAG, "网络连接状态 = " + getStateTip(state));

		switch (state) {
		case TelephonyManager.DATA_DISCONNECTED:// 网络断开
			break;
		case TelephonyManager.DATA_CONNECTING:// 网络正在连接
			break;
		case TelephonyManager.DATA_CONNECTED:// 网络连接上
			notificationService.connect();
			break;
		case TelephonyManager.DATA_SUSPENDED:// 暂停的，悬浮的(如在2G网络下：电话来的，网络数据暂停)
			break;
		}
	}

	/**
	 * 根据网络状态码获得状态描述
	 * 
	 * @param state
	 *            状态码
	 * @return 状态描述
	 */
	private String getStateTip(int state) {
		switch (state) {
		case TelephonyManager.DATA_DISCONNECTED:// 网络断开
			return "网络断开";
		case TelephonyManager.DATA_CONNECTING:// 网络正在连接
			return "网络正在连接";
		case TelephonyManager.DATA_CONNECTED:// 网络连接上
			return "网络连接上";
		case TelephonyManager.DATA_SUSPENDED:// 暂停的，悬浮的(如在2G网络下：电话来的，网络数据暂停)
			return "暂停的，悬浮的(如在2G网络下：电话来的，网络数据暂停)";
		}
		return "网络状态未知";
	}

}
