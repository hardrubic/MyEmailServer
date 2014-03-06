package server.smtp.command;

import java.util.ArrayList;

import server.antispam.AntiMain;
import server.bin.EmailServerStart;

import server.send.MailQueue;
import server.smtp.EmailPersistence;
import server.smtp.SMTPException;
import server.smtp.SMTPSession;
import server.util.DebugUtil;

public class DATACommand implements SMTPCommandHandler {

	@Override
	public void onCommand(SMTPSession session) throws SMTPException {
		if (!session.checkCommandOrder()) {
			throw new SMTPException("502 command not implemented");
		}

		// 获取参数
		String remainCommand = session.getCurRemainCommandStr();
		if ((remainCommand != null) && (remainCommand.indexOf(":") > 0)) {
			// 带参数，错误
			throw new SMTPException(
					"500 unexpected argument provided with data command");
		}

		// 获取邮件内容
		session.sendResponse("354 start mail input;end with<CRLF>.<CRLF>");
		String content=getEmailContent(session);

		// 检查内容的长度
		char[] charArray = content.toString().toCharArray();
		long emailSize = Long.parseLong((String) EmailServerStart.emailConf
				.get("emailSize"));
		if (charArray.length > emailSize) {
			// 邮件正文过长
			throw new SMTPException("552  mail data is to long");
		}
		//邮件初始化、设置邮件内容
		session.getEmail().setContent(content.toString());
		session.getEmail().initEmail();

		// 垃圾邮件检测
		AntiMain antiMain = AntiMain.getInstance();
		if (!antiMain.isSpam(session.getEmail())) {
			// 保存文件到磁盘上
			EmailPersistence ep = EmailPersistence.getInstance();
			ep.saveEmail(session.getEmail());
			// 添加邮件到发送队列
			MailQueue mailQueue = MailQueue.getInstance();
			mailQueue.addEmailToSendQueue(session.getEmail());

			DebugUtil.printEmail(session.getEmail());
		} else {
			// 是垃圾邮件
			session.sendResponse("550 refuse to accept");
			return;
		}
		session.sendResponse("250 OK");
		// 重置缓存
		session.resetState();
	}

	/**
	 * 读取邮件内容
	 * 
	 * @return
	 */
	private String getEmailContent(SMTPSession session) {
		String msgIn = "";
		ArrayList<String> emailData = new ArrayList<String>();
		while (!msgIn.equals(".")) {
			if (msgIn != "") {
				emailData.add(msgIn);
				emailData.add("\n");
			}
			msgIn = session.getCommand(session.getIn(),
					session.getSmtpConnectLostTime());
		}
		emailData.remove(emailData.size() - 1);

		// 读取内容完毕
		StringBuffer content = new StringBuffer();
		for (int i = 0; i < emailData.size(); i++) {
			content.append(emailData.get(i));
		}
		return content.toString();
	}
}
