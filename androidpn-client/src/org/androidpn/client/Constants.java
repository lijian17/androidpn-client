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
 * 静态常量
 * 
 * @author lijian
 * @date 2016-7-24 上午8:26:23
 */
public class Constants {

	/** SharedPreferences 文件名 **/
	public static final String SHARED_PREFERENCE_NAME = "client_preferences";

	/********************************** 偏好设置 ***************************************************************************************************/

	/** 回调Activity的包名 **/
	public static final String CALLBACK_ACTIVITY_PACKAGE_NAME = "CALLBACK_ACTIVITY_PACKAGE_NAME";

	/** 回调Activity的全类名 **/
	public static final String CALLBACK_ACTIVITY_CLASS_NAME = "CALLBACK_ACTIVITY_CLASS_NAME";

	/** XMPP密钥 **/
	public static final String API_KEY = "API_KEY";

	/** 版本号 **/
	public static final String VERSION = "VERSION";

	/** XMPP的IP **/
	public static final String XMPP_HOST = "XMPP_HOST";

	/** XMPP的端口 **/
	public static final String XMPP_PORT = "XMPP_PORT";

	/** XMPP的用户名 **/
	public static final String XMPP_USERNAME = "XMPP_USERNAME";

	/** XMPP的密码 **/
	public static final String XMPP_PASSWORD = "XMPP_PASSWORD";
	
	/** XMPP通信协议的名称空间 **/
	public static final String XMPP_PROTOCOL_NAMESPACE = "androidpn:iq:notification";

	/** XMPP通信协议的节点名 **/
	public static final String XMPP_PROTOCOL_ELEMENTNAME = "notification";
	
	/** XMPP通信协议的“消息回执”的名称空间 **/
	public static final String XMPP_PROTOCOL_NAMESPACE_DELIVERCONFIRM = "androidpn:iq:deliverconfirm";

	/** XMPP通信协议“消息回执”的节点名 **/
	public static final String XMPP_PROTOCOL_DELIVERCONFIRM = "deliverconfirm";
	
	/** XMPP通信协议的“设置别名”的名称空间 **/
	public static final String XMPP_PROTOCOL_NAMESPACE_SETALIAS = "androidpn:iq:setalias";
	
	/** XMPP通信协议“设置别名”的节点名 **/
	public static final String XMPP_PROTOCOL_SETALIAS = "setalias";
	
	/** XMPP通信协议的“设置标签集”的名称空间 **/
	public static final String XMPP_PROTOCOL_NAMESPACE_SETTAGS = "androidpn:iq:settags";
	
	/** XMPP通信协议“设置标签集”的节点名 **/
	public static final String XMPP_PROTOCOL_SETTAGS = "settags";

	// public static final String USER_KEY = "USER_KEY";

	/** 设备ID(手机*#06#) **/
	public static final String DEVICE_ID = "DEVICE_ID";

	/** 模拟器ID **/
	public static final String EMULATOR_DEVICE_ID = "EMULATOR_DEVICE_ID";

	/** 通知的logo图片 **/
	public static final String NOTIFICATION_ICON = "NOTIFICATION_ICON";

	/** 是否开机自启动 **/
	public static final String SETTINGS_AUTO_START = "SETTINGS_AUTO_START";
	
	/** 是否显示推送的通知 **/
	public static final String SETTINGS_NOTIFICATION_ENABLED = "SETTINGS_NOTIFICATION_ENABLED";

	/** 当接到推送通知-->是否播放通知声音 **/
	public static final String SETTINGS_SOUND_ENABLED = "SETTINGS_SOUND_ENABLED";

	/** 当接到推送通知-->是否震动手机 **/
	public static final String SETTINGS_VIBRATE_ENABLED = "SETTINGS_VIBRATE_ENABLED";

	/** 当接到推送通知-->是否显示吐司 **/
	public static final String SETTINGS_TOAST_ENABLED = "SETTINGS_TOAST_ENABLED";

	/********************************** 通知 ***************************************************************************************************/

	/** 通知的ID **/
	public static final String NOTIFICATION_ID = "NOTIFICATION_ID";

	/** 通知的密钥 **/
	public static final String NOTIFICATION_API_KEY = "NOTIFICATION_API_KEY";

	/** 通知的标题 **/
	public static final String NOTIFICATION_TITLE = "NOTIFICATION_TITLE";

	/** 通知的正文 **/
	public static final String NOTIFICATION_MESSAGE = "NOTIFICATION_MESSAGE";

	/** 通知的Url **/
	public static final String NOTIFICATION_URI = "NOTIFICATION_URI";
	
	/** 通知的图片地址 **/
	public static final String NOTIFICATION_IMAGE_URL = "NOTIFICATION_IMAGE_URL";

	/********************************** intent动作 ***************************************************************************************************/

	/** 显示通知 **/
	public static final String ACTION_SHOW_NOTIFICATION = "org.androidpn.client.SHOW_NOTIFICATION";

	/** 通知被点击 **/
	public static final String ACTION_NOTIFICATION_CLICKED = "org.androidpn.client.NOTIFICATION_CLICKED";

	/** 清除通知 **/
	public static final String ACTION_NOTIFICATION_CLEARED = "org.androidpn.client.NOTIFICATION_CLEARED";

}
