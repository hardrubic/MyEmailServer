package server.send;

import java.util.TimerTask;

import org.apache.log4j.Logger;

import server.bin.EmailServerStart;

import server.model.Email;

public class SendQueueTimerTask extends TimerTask {
	private SendEmail sendEmail;
	private MailQueue mailQueue;
	private SendQueueTimerTask sendQueueTimerTask;
	private Email email;
	private Logger logger;

	public SendQueueTimerTask() {
		sendEmail = SendEmail.getInstance();
		mailQueue = MailQueue.getInstance();
		logger=EmailServerStart.sendLog;
		logger.info("send:启动sendQueue");
	}

	@Override
	public void run() {
		if (!mailQueue.isSendQueueEmpty()) {
			// 取出一封邮件
			email = mailQueue.getEmailFromSendQueue();
			// 发送邮件
			sendEmail.sendEmail(email);
		}else {
//			logger.info("sendQueue没有待发送邮件");
		}
	}
}
