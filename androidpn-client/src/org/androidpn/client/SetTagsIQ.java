package org.androidpn.client;

import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.packet.IQ;

/**
 * 标签集实体
 * 
 * @author lijian
 * @date 2016-12-10 下午4:58:05
 */
public class SetTagsIQ extends IQ {

	/** 用户名 */
	private String username;

	/** 标签集合 */
	private List<String> tagList = new ArrayList<String>();

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public List<String> getTagList() {
		return tagList;
	}

	public void setTagList(List<String> tagList) {
		this.tagList = tagList;
	}

	@Override
	public String getChildElementXML() {
		StringBuilder sb = new StringBuilder();
		sb.append("<").append(Constants.XMPP_PROTOCOL_SETTAGS)
				.append(" xmlns=\"")
				.append(Constants.XMPP_PROTOCOL_NAMESPACE_SETTAGS)
				.append("\">");
		if (username != null) {
			sb.append("<username>").append(username).append("</username>");
		}
		if (tagList != null && !tagList.isEmpty()) {
			sb.append("<tags>");
			boolean needSeperate = false;
			for (String tag : tagList) {
				if (needSeperate) {
					sb.append(",");
				}
				sb.append(tag);
				needSeperate = true;
			}
			sb.append("</tags>");
		}
		sb.append("</").append(Constants.XMPP_PROTOCOL_SETTAGS).append("> ");
		return sb.toString();
	}
}
