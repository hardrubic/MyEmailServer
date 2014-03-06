package server.pop;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;
import org.apache.log4j.Logger;
import server.bin.EmailServerStart;
import server.bin.UserListManager;
import server.model.User;
import server.pop.command.POP3CommandHandler;
import server.util.EnumUtil.LockType;
import server.util.EnumUtil.POP3State;
import server.util.TimeoutUtil;

public class POP3Session implements Runnable {
	/**
	 * 当前状态
	 */
	POP3State state;

	String[] commandList = new String[3];
	/**
	 * TCP socket
	 */
	Socket clientSocket;
	/**
	 * IO
	 */
	PrintWriter out;
	/**
	 * IO
	 */
	BufferedReader in;
	/**
	 * 客户端host
	 */
	String remoteHost;
	/**
	 * 客户端IP
	 */
	String remoteIP;
	/**
	 * session是否结束
	 */
	boolean sessionEnded;

	/**
	 * 失去pop3连接的时间
	 */
	long connectLostTime;

	/**
	 * ID
	 */
	String pop3ID;
	/**
	 * 本服务器的域名
	 */
	String[] localDomainList;

	/**
	 * 用于生成pop3ID
	 */
	private final static Random random = new Random();

	/**
	 * 日记记录
	 */
	Logger logger;
	/**
	 * 用户列表
	 */
	private UserListManager userListManager;
	/**
	 * 尝试登陆的用户
	 */
	String loginUserName = "";
	/**
	 * 当前用户
	 */
	User user;

	public POP3Session(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}

	/**
	 * 初始化smtp服务线程
	 */
	public void initSession() {
		try {
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			connectLostTime = Long.parseLong(EmailServerStart.emailConf.getProperty("connectLostTime"));
			localDomainList = getDomainList();
			remoteHost = clientSocket.getInetAddress().getHostName();
			remoteIP = clientSocket.getInetAddress().getHostAddress();
			sessionEnded = false;
			pop3ID = random.nextInt(1024) + "";
			logger = EmailServerStart.pop3Log;
			state = POP3State.AUTHORIZATION_READY;
			userListManager = UserListManager.getInstance();
			loginUserName = "";
		} catch (IOException e) {
			getLogger().error("pop3:启动服务失败");
			return;
		}
	}

	@Override
	public void run() {
		String responseStr = null;
		String strInputMesg = null;
		POP3CommandHandler ch = null;

		// 初始化pop3 session
		initSession();
		// pop3服务线程正常启动
		sendResponse("+OK pop3 server is ready");
		getLogger().info("pop3:建立一个连接" + "(" + pop3ID + "/" + remoteHost + "/" + remoteIP + ")");

		// 循环等待命令
		while (!sessionEnded) {
			// 读取一行数据
			strInputMesg = getCommand(in, connectLostTime);

			if (strInputMesg == null) {
				// 超时没有读取命令，断开连接
				getLogger().info("pop3:超时读取客户端命令");
				sessionEnded = true;
				// 如果用户被锁定，则解锁
				if (null != user && userListManager.isUserLocked(user.getUserName(), LockType.POP3)) {
					userListManager.unlockUser(user.getUserName(), LockType.POP3);
				}
				break;
			}

			// 检查命令格式
			if (checkCommandValid(strInputMesg)) {
				// 正确
				ch = POP3DealCommand.getCommandHandler(commandList[0]);
				if (ch == null) {
					// 非法命令
					responseStr = "-ERR unknown command";
					sendResponse(responseStr);
					getLogger().info(responseStr);
				} else {
					try {
						ch.onCommand(this, commandList[1], commandList[2]);
					} catch (POP3Exception e) {
						System.out.println("通过exception发送一条错误信息");
						sendResponse(e.getErrorMsg());
					}
				}
			} else {
				// 不正确
				sendResponse("-ERR unknown command");
			}
		}
		if (user != null) {
			getLogger().info("pop3: ID:" + pop3ID + "  user:" + user.getUserName() + "断开连接");
		}

		clearSession();
	}

	/**
	 * 检查命令是否有效
	 * 
	 * @param str
	 * @return
	 */
	public boolean checkCommandValid(String str) {
		if (str == null) {
			return false;
		}
		String command = str;
		String[] temp = command.split(" ");
		int num = temp.length;
		if (num == 0) {
			return false;
		} else if (num > 0) {
			// 获取命令，并且大写
			command = temp[0].toUpperCase();
		}
		String argument1 = null;
		if (num > 1) {
			// 获取第一个参数
			argument1 = temp[1];
		}
		String argument2 = null;
		if (num > 2) {
			// 获取第二个参数
			argument2 = temp[2];
		}
		// 保存处理好的命令
		commandList[0] = command;
		commandList[1] = argument1;
		commandList[2] = argument2;
		return true;
	}

	/**
	 * 重置状态
	 */
	public void resetState() {

	}

	/**
	 * 在限定时间里获取命令
	 * 
	 * @param in
	 * @param timeout
	 * @return
	 */
	public String getCommand(BufferedReader in, long timeout) {
		String strInputMesg = TimeoutUtil.limitTimeReadLine(in, timeout);
		getLogger().debug("pop3读取：" + strInputMesg);
		return strInputMesg;
	}

	/**
	 * 服务端回复
	 * 
	 * @param out
	 * @param response
	 * @return
	 */
	public boolean sendResponse(String response) {
		logger.debug("pop3 回应:" + response);
		out.println(response);
		out.flush();
		return true;
	}

	/**
	 * 获取本服域名名称
	 * 
	 * @return
	 */
	public String[] getDomainList() {
		String str = EmailServerStart.emailConf.getProperty("localDomain");
		return str.split(",");
	}

	/**
	 * 结束pop3 session
	 */
	public void clearSession() {
		if (out != null) {
			out.close();
		}
		if (in != null) {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (clientSocket != null) {
			try {
				clientSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public PrintWriter getOut() {
		return out;
	}

	public String[] getCommandList() {
		return commandList;
	}

	public String getRemoteHost() {
		return remoteHost;
	}

	public String getRemoteIP() {
		return remoteIP;
	}

	public String getPop3ID() {
		return pop3ID;
	}

	public void setSessionEnded(boolean sessionEnded) {
		this.sessionEnded = sessionEnded;
	}

	public Logger getLogger() {
		return logger;
	}

	public String[] getLocalDomainList() {
		return localDomainList;
	}

	public BufferedReader getIn() {
		return in;
	}

	public long getConnectLostTime() {
		return connectLostTime;
	}

	public POP3State getState() {
		return state;
	}

	public void setState(POP3State state) {
		this.state = state;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public UserListManager getUserList() {
		return userListManager;
	}

	public String getLoginUserName() {
		return loginUserName;
	}

	public void setLoginUserName(String loginUserName) {
		this.loginUserName = loginUserName;
	}

}
