package server.send;

import java.io.*;
import java.net.*;
import java.util.*;

import org.apache.log4j.Logger;

import server.smtp.EmailPersistence;
import server.util.CheckUtil;
import server.util.DnsUtil;
import server.util.EmailUtil;
import server.util.StringUtil;
import server.util.TimeoutUtil;
import server.bin.EmailServerStart;
import server.bin.UserListManager;
import server.model.Email;
import server.model.User;

/**
 * 邮件发送模块
 * 
 * @author heng
 * 
 */
public class SendEmail {
	/**
	 * 发送类型
	 * 
	 * @author heng
	 * 
	 */
	public enum SendType {
		TO, CC, BCC, BACK
	}

	/**
	 * 单例
	 */
	private static SendEmail sendEmail;
	/**
	 * 日记记录
	 */
	private Logger logger;
	/**
	 * 邮件队列
	 */
	private MailQueue mailQueue;
	/**
	 * 本机域名
	 */
	private String localDomain[];
	/**
	 * 计时器
	 */
	private Timer timer;
	/**
	 * 用户列表
	 */
	private UserListManager userListManager = UserListManager.getInstance();

	/**
	 * 发送邮件出错异常
	 * 
	 * @author heng
	 * 
	 */
	public class SendEmailToOtherException extends Exception {
		private static final long serialVersionUID = 1L;
		private String errorMsg;

		public SendEmailToOtherException(String e) {
			errorMsg = e;
		}

		public String getErrorMeg() {
			return errorMsg;
		}
	}

	private SendEmail() {
		// 记录javamail发送过程
		logger = EmailServerStart.sendLog;

		localDomain = EmailServerStart.emailConf.getProperty("localDomain").split(",");
	}

	public static SendEmail getInstance() {
		if (sendEmail == null) {
			sendEmail = new SendEmail();
		}
		return sendEmail;
	}

	public void start() {
		// 初始化邮件队列
		mailQueue = MailQueue.getInstance();
		// 加载磁盘上未发送的邮件
		loadEmailPersistence();
		// 启动计时器
		timer = new Timer();
		timer.scheduleAtFixedRate(new SendQueueTimerTask(), new Date(), 1000);
		timer.scheduleAtFixedRate(new WaitQueueTimerTask(), new Date(), 10000);
	}

	public void stop() {
		timer.cancel();
	}

	/**
	 * 邮件初始化，判断邮件的目的域名
	 * 
	 * @param email
	 */
	public void sendEmail(Email email) {
		// 邮件clean

		// 获取未发送的收件人列表
		List<String> recipientList = email.getUnsendList();
		// 遍历收件人，尝试发送
		for (Iterator iterator = recipientList.iterator(); iterator.hasNext();) {
			String address = (String) iterator.next();
			String emailDomain = StringUtil.getEmailDomain(address);
			if (CheckUtil.isSameDomain(emailDomain, localDomain)) {
				// 邮件属于本域
				sendToLocal(address, email);
			} else {
				// 邮件属于外域
				sendToOther(address, email);
			}
		}
		// 检查是否全部发送成功
		if (email.isAllSend()) {
			logger.info("send:邮件" + email.getUidl() + "全部发送成功");
		} else {
			// 将邮件加入等待队列
			mailQueue.addEmailToWaitQueue(email);
		}
	}

	/**
	 * 发送到本域的邮件
	 * 
	 * @return
	 */
	public void sendToLocal(String toAddress, Email email) {
		logger.info("send:向本域的<" + toAddress + ">发送了一封邮件");
		// 将邮件保存在本域的userList
		String userName = StringUtil.getEmailName(toAddress);
		if (!userListManager.checkUserExist(userName)) {
			// 用户不存在
			logger.info("send:邮件" + email.getUidl() + " 发送到本域地址<" + toAddress + ">失败，原因：用户不存在");
			return;
		}
		// 保存邮件，等待接收
		User user = userListManager.getUser(userName);
		user.getEmailList().add(email);

		// 将用户列表写进磁盘
		UserListManager userListManager = UserListManager.getInstance();
		userListManager.writeUserList();

		// 标记发送成功
		email.markSendSuc(toAddress);
		clearEmailPersistence(email);
	}

	/**
	 * 发送到外域的邮件
	 * 
	 * @return
	 * @throws NoSuchProviderException
	 */
	public void sendToOther(String toAddress, Email email) {
		boolean isConnectToServer = false;
		long connectResponseTime = Long.parseLong(EmailServerStart.emailConf.getProperty("connectResponseTime"));
		try {
			// 获取服务器
			List<String> serverList = DnsUtil.getMailServer(StringUtil.getEmailDomain(toAddress));
			if (serverList == null) {
				throw new SendEmailToOtherException("can't find mail server");
			}
			// 准备TCP连接
			BufferedReader reply = null;
			PrintWriter send = null;
			Socket sock = null;
			for (int i = 0; i < serverList.size(); i++) {
				try {
					sock = new Socket(serverList.get(i), 25);
					reply = new BufferedReader(new InputStreamReader(sock.getInputStream()));
					send = new PrintWriter(sock.getOutputStream());
				} catch (UnknownHostException e) {
					throw new SendEmailToOtherException("can't find mail server");
				} catch (IOException e) {
					throw new SendEmailToOtherException("can't connect to mail server");
				}
				// 读取邮件服务器信息
				String response = getResponse(reply, connectResponseTime);
				if (!response.startsWith("220")) {
					continue;
				}
				// 连接到server
				isConnectToServer = true;

				String localHostName = EmailServerStart.emailConf.getProperty("localHostName");
				sendReply(send, "HELO " + localHostName, false);
				response = getResponse(reply, connectResponseTime);
				if (!response.startsWith("250")) {
					throw new SendEmailToOtherException("helo error:" + response);
				}

				String fromAddress = EmailUtil.getFrom(email);
				sendReply(send, "mail from:<" + fromAddress + ">", false);
				response = getResponse(reply, connectResponseTime);
				if (!response.startsWith("250")) {
					throw new SendEmailToOtherException("mail from error:" + response);
				}

				sendReply(send, "rcpt to:<" + toAddress + ">", false);
				response = getResponse(reply, connectResponseTime);
				if (!response.startsWith("250")) {
					throw new SendEmailToOtherException("rcpt to error:" + response);
				}

				sendReply(send, "data", false);
				response = getResponse(reply, connectResponseTime);
				if (!response.startsWith("354")) {
					throw new SendEmailToOtherException("data error:" + response);
				}

				sendReply(send, email.getContent() + "\r\n.\r\n", true);
				response = getResponse(reply, connectResponseTime);
				if (!response.startsWith("250")) {
					throw new SendEmailToOtherException("data content error:" + response);
				}
				// 发送成功
				break;
			}
			if (isConnectToServer == false) {
				// 不能连接到server，邮件发送失败
				throw new SendEmailToOtherException("can't connect to server");
			} else {
				// 邮件发送成功
				logger.info("send:邮件" + email.getUidl() + " 发送到地址<" + toAddress + ">成功");
				// 更新文件状态到磁盘上
				EmailPersistence ep = EmailPersistence.getInstance();
				ep.saveEmail(email);
				// 标记发送成功
				email.markSendSuc(toAddress);
				clearEmailPersistence(email);
			}
		} catch (SendEmailToOtherException e) {
			// 邮件发送失败
			logger.info("send:邮件" + email.getUidl() + " 发送到地址<" + toAddress + ">失败，原因：" + e.getErrorMeg());
			return;
		}
	}

	/**
	 * 创建新的邮件回退
	 * 
	 * @param email
	 * @param unsendAddress
	 */
	public void sendBackEmail(Email email) {
		// 获取回退邮件
		Email backEmail = CreateBackEmail.getBackEmail(email);
		String backToAddress = EmailUtil.getFrom(email);
		logger.info("send:将邮件" + email.getUidl() + "回退到<" + EmailUtil.getFrom(email) + ">");
		sendToLocal(backToAddress, backEmail);

		// 删除磁盘邮件文件
		EmailPersistence.getInstance().deleteEmail(email.getUidl());
	}

	/**
	 * 在限定时间里获取命令
	 * 
	 * @param in
	 * @param timeout
	 * @return
	 */
	public String getResponse(BufferedReader in, long timeout) {
		String response = TimeoutUtil.limitTimeReadLine(in, timeout);
		logger.debug("send获取:" + response);
		if (response == null) {
			return "";
		}
		return response;
	}

	/**
	 * 回复服务器
	 * 
	 * @param out
	 * @param reply
	 * @param flag
	 * @return
	 */
	public boolean sendReply(PrintWriter out, String reply, boolean flag) {
		out.println(reply);
		out.flush();
		if (flag == false) {
			logger.debug("send发送：" + reply);
		} else {
			logger.debug("send发送:" + "邮件正文");
		}
		return true;
	}

	/**
	 * 如果邮件已经成功发送给所有收件人，清理磁盘上的邮件文件
	 * 
	 * @param uidl
	 */
	public void clearEmailPersistence(Email email) {
		EmailPersistence ep = EmailPersistence.getInstance();
		if (email.isAllSend()) {
			ep.deleteEmail(email.getUidl());
		}
	}

	/**
	 * 加载存在磁盘上未发送的邮件
	 */
	public void loadEmailPersistence() {
		EmailPersistence ep = EmailPersistence.getInstance();
		List<Email> emailList = ep.loadEmail();
		// 将邮件放入发送列表
		if (emailList != null) {
			for (int i = 0; i < emailList.size(); i++) {
				Email email = emailList.get(i);
				mailQueue.addEmailToSendQueue(email);
			}
		}
	}
}
