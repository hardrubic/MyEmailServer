package server.smtp;

import server.bin.EmailServerStart;

public class SMTPLoad implements Runnable {
	// smtp端口
	private String[] strSmtpPorts;

	@Override
	public void run() {
		// 获取smtp端口
		String smtpPorts = EmailServerStart.emailConf.getProperty("smtpPort");
		if(smtpPorts==null){
			EmailServerStart.smtpLog.error("smtp:无法初始化smtp端口");
			return;
		}
		strSmtpPorts = EmailServerStart.emailConf.getProperty("smtpPort")
				.split(",");
		
		for (String port : strSmtpPorts) {
			//EmailServerStart.log.info("监听smtp端口：" + port);
			new Thread(new SMTPStart(Integer.parseInt(port))).start();
		}

	}

}
