package org.androidpn.client;

import org.jivesoftware.smack.packet.IQ;

/**
 * 别名实体
 * 
 * @author lijian
 * @date 2016-12-10 下午12:23:46
 */
public class SetAliasIQ extends IQ {

	/** 用户名 */
	private String username;

	/** 别名 */
	private String alias;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	@Override
	public String getChildElementXML() {
		StringBuilder sb = new StringBuilder();
		sb.append("<").append(Constants.XMPP_PROTOCOL_SETALIAS)
				.append(" xmlns=\"")
				.append(Constants.XMPP_PROTOCOL_NAMESPACE_SETALIAS)
				.append("\">");
		if (username != null) {
			sb.append("<username>").append(username).append("</username>");
		}
		if (alias != null) {
			sb.append("<alias>").append(alias).append("</alias>");
		}
		sb.append("</").append(Constants.XMPP_PROTOCOL_SETALIAS).append("> ");
		return sb.toString();
	}
}
