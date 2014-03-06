package server.send;

import java.util.List;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import server.bin.EmailServerStart;

import server.model.Email;

public class WaitQueueTimerTask extends TimerTask {
	private SendEmail sendEmail;
	private MailQueue mailQueue;
	private WaitQueueTimerTask waitQueueTimerTask;
	private Email email;
	private Logger logger;
	
	public WaitQueueTimerTask(){
		sendEmail=SendEmail.getInstance();
		mailQueue=MailQueue.getInstance();
		logger=EmailServerStart.sendLog;
		logger.info("send:启动waitQueue");
	}

	@Override
	public void run() {
		if (!mailQueue.isWaitQueueEmpty()) {
			// 取出一封邮件
			email = mailQueue.getEmailFromWaitQueue();
			//查询发送次数
			if (mailQueue.reachMaxSendTime(email)) {
				logger.info("send:邮件"+email.getUidl()+"到达尝试发送最大次数");
				//到达发送次数，回退邮件
				mailQueue.removeSendTime(email);
				sendEmail.sendBackEmail(email);
			}else {
				sendEmail.sendEmail(email);
				//记录次数
				mailQueue.addSendTime(email);
				logger.info("send:从waitQueue发送一封邮件");
			}
		}else {
//			logger.info("waitQueue没有待发送邮件");
		}
	}
}
