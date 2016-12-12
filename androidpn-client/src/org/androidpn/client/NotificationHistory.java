package org.androidpn.client;

import org.litepal.crud.DataSupport;

/**
 * 通知历史实体
 * 
 * @author lijian
 * @date 2016-12-11 下午7:32:37
 */
public class NotificationHistory extends DataSupport {

	private String apiKey;

	private String title;

	private String message;

	private String uri;

	private String imageUrl;

	private String time;

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

}
