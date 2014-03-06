package server.pop;

import server.bin.EmailServerStart;

public class POP3Load implements Runnable {
	// pop3端口
	private String[] strPop3Ports;

	@Override
	public void run() {
		// 获取pop3端口
		String pop3Ports = EmailServerStart.emailConf.getProperty("pop3Port");
		if(pop3Ports==null){
			EmailServerStart.pop3Log.error("pop3:无法初始化端口");
			return;
		}
		strPop3Ports = pop3Ports.split(",");
		
		for (String port : strPop3Ports) {
			//EmailServerStart.log.info("监听smtp端口：" + port);
			new Thread(new POP3Start(Integer.parseInt(port))).start();
		}

	}

}
