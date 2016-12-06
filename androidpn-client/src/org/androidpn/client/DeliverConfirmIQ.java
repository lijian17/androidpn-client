package org.androidpn.client;

import org.jivesoftware.smack.packet.IQ;

/**
 * 消息回执实体
 * 
 * @author lijian
 * @date 2016-12-6 下午11:09:48
 */
public class DeliverConfirmIQ extends IQ {

	private String uuid;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	@Override
	public String getChildElementXML() {
		StringBuilder sb = new StringBuilder();
		sb.append("<").append(Constants.XMPP_PROTOCOL_DELIVERCONFIRM)
				.append(" xmlns=\"")
				.append(Constants.XMPP_PROTOCOL_NAMESPACE_DELIVERCONFIRM)
				.append("\">");
		if (uuid != null) {
			sb.append("<uuid>").append(uuid).append("</uuid>");
		}
		sb.append("</").append(Constants.XMPP_PROTOCOL_DELIVERCONFIRM)
				.append("> ");
		return sb.toString();
	}

}
