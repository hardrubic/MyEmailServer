package server.pop;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import server.bin.EmailServerStart;

public class POP3Start implements Runnable {
	private int intPort;
	private ServerSocket serverSocket = null;
	private Socket clientSocket = null;

	public POP3Start(int intPort) {
		this.intPort = intPort;
	}

	@Override
	public void run() {
		try {
			serverSocket = new ServerSocket(intPort);
		} catch (IOException e) {
			EmailServerStart.pop3Log.error("pop3:无法监听" + intPort + "端口");
			System.exit(0);
			return;
		}

		// 监听端口
		EmailServerStart.pop3Log.info("pop3:监听端口:" + intPort);

		// 等待pop3连接
		while (EmailServerStart.isServerRun) {
			try {
				//EmailServerStart.log.info("等待pop3连接");
				clientSocket = serverSocket.accept();
				new Thread(new POP3Session(clientSocket)).start();
			} catch (IOException e) {
				EmailServerStart.pop3Log.error("pop3:服务异常出错");
			}

		}
		EmailServerStart.pop3Log.info("pop3:停止监听端口");
	}
}
