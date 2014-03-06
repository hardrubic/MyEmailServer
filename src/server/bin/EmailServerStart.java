package server.bin;

import java.io.*;
import java.util.Properties;

import org.apache.log4j.Logger;

import server.pop.POP3Load;

import server.send.SendEmail;
import server.smtp.SMTPLoad;

public class EmailServerStart {
	public static Properties emailConf;
	public static Logger smtpLog = Logger.getLogger(EmailServerStart.class.getName());
	public static Logger pop3Log = Logger.getLogger(EmailServerStart.class.getName());
	public static Logger sendLog = Logger.getLogger(EmailServerStart.class.getName());
	public static Logger persistenceLog = Logger.getLogger(EmailServerStart.class.getName());
	public static Logger antispamLog = Logger.getLogger(EmailServerStart.class.getName());
	public static Logger serverLog = Logger.getLogger(EmailServerStart.class.getName());
	
	public static boolean isServerRun=false;
	
	public static boolean startEmailServer(){
		//加载配置文件
	    emailConf =new Properties();
		try {
			emailConf.load(new FileInputStream("src/server/bin/emailConf.properties"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		isServerRun=true;
		//初始化用户列表
		UserListManager userListManager=UserListManager.getInstance();
		userListManager.initUserList();
		
		new Thread(new SMTPLoad()).start();
		new Thread(new POP3Load()).start();
		//启动邮件发送模块
		SendEmail sendEmail = SendEmail.getInstance();
		sendEmail.start();
		
		return true;
	}
	
	public static boolean stopEmailServer(){
		isServerRun=false;
		return true;
	}
	
	public static void main(String[] args) {
		startEmailServer();
	}
}
