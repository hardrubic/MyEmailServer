package server.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import org.apache.james.mime4j.dom.Message;
import org.apache.james.mime4j.message.MessageImpl;

import server.send.SendEmail;
import server.send.SendEmail.SendType;
import server.util.EmailUtil;
import server.util.StringUtil;

public class Email implements Serializable {
	private String uidl; // 唯一标识码
	private Set<String> recipientsList;// 初始收件人地址
	private String content; // 邮件原文
	private Map<String, Boolean> sendStatusMap; // 标记发送状况
	private transient boolean isDeleted; // 是否标记被删除

	public Email() {
		isDeleted = false;
		sendStatusMap = new HashMap<String, Boolean>();
		recipientsList = new HashSet<String>();
		// 产生唯一识别码
		Random r = new Random();
		String nowTime = String.valueOf(System.currentTimeMillis());
		uidl = r.nextInt() + nowTime + r.nextInt();
	}

	/**
	 * 邮件输入内容后，初始化参数
	 */
	public void initEmail() {
		List<String> recipientsList = EmailUtil.getAllRecipients(this);
		for (int i = 0; i < recipientsList.size(); i++) {
			sendStatusMap.put(recipientsList.get(i), false);
		}
	}

	/**
	 * 标记邮件已经成功发送到某一个收件人
	 * 
	 * @param address
	 * @param type
	 */
	public void markSendSuc(String address) {
		synchronized (sendStatusMap) {
			if (sendStatusMap.remove(address) != null) {
				sendStatusMap.put(address, true);
			}
		}
	}

	/**
	 * 获取已经发送的地址
	 * 
	 * @return
	 */
	public List<String> getSentAddress() {
		Set<String> sentList = new HashSet<String>();
		Iterator<Entry<String, Boolean>> iter = sendStatusMap.entrySet()
				.iterator();
		while (iter.hasNext()) {
			// 拿出一对键值对
			Map.Entry entry = (Map.Entry) iter.next();
			String address = (String) entry.getKey();
			boolean flag = (Boolean) entry.getValue();
			if (flag == true) {
				sentList.add(address);
			}
		}
		return new ArrayList<String>(sentList);
	}

	/**
	 * 获取未发送成功的地址
	 * 
	 * @return
	 */
	public List<String> getUnsendList() {
		Set<String> unsendList = new HashSet<String>();
		Iterator<Entry<String, Boolean>> iter = sendStatusMap.entrySet()
				.iterator();
		while (iter.hasNext()) {
			// 拿出一对键值对
			Map.Entry entry = (Map.Entry) iter.next();
			String address = (String) entry.getKey();
			boolean flag = (Boolean) entry.getValue();
			if (flag == false) {
				unsendList.add(address);
			}
		}
		return new ArrayList<String>(unsendList);
	}

	/**
	 * 是否已经发送给所有收件人
	 * 
	 * @return
	 */
	public boolean isAllSend() {
		boolean flag = false;
		if (sendStatusMap.containsValue(false)) {
			return false;
		}
		return true;
	}

	public Set<String> getRecipientsList() {
		return recipientsList;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getUidl() {
		return uidl;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}
}
