package server.smtp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import server.send.MailQueue;

import server.bin.EmailServerStart;

public class SMTPStart implements Runnable {
	private int intPort;
	private ServerSocket serverSocket = null;
	private Socket clientSocket = null;

	public SMTPStart(int intPort) {
		this.intPort = intPort;
	}

	@Override
	public void run() {
		try {
			serverSocket = new ServerSocket(intPort);
		} catch (IOException e) {
			EmailServerStart.smtpLog.error("smtp:无法监听" + intPort + "端口");
			System.exit(0);
			return;
		}

		// 监听端口
		EmailServerStart.smtpLog.info("stmp:监听端口:" + intPort);

		// 等待smtp连接
		while (EmailServerStart.isServerRun) {
			try {
				//EmailServerStart.log.info("等待smtp连接");
				clientSocket = serverSocket.accept();
				new Thread(new SMTPSession(clientSocket)).start();
			} catch (IOException e) {
				EmailServerStart.smtpLog.error("smtp:smtp服务异常出错");
			}

		}
		EmailServerStart.smtpLog.info("smtp:停止监听smtp端口");
	}
}
