package server.smtp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;

import org.apache.log4j.Logger;

import server.model.Email;

import server.smtp.command.SMTPCommandHandler;
import server.util.TimeoutUtil;

import server.bin.EmailServerStart;


public class SMTPSession implements Runnable{
	/**
	 * TCP socket
	 */
	private Socket clientSocket;
	
	/**
	 * IO
	 */
	private PrintWriter out;
	
	/**
	 * IO
	 */
	private BufferedReader in;
	
	/**
	 * 当前命令名称
	 */
	private String curCommandName;
	
	/**
	 * 截取命令名称后剩余的字段
	 */
	private String curRemainCommandStr;
	
	/**
	 * 上一个命令名称
	 */
	private String lastCommandName;

	/**
	 * 邮件model
	 */
	private Email email;
	
	/**
	 * 客户端host
	 */
	private String remoteHost;
	
	/**
	 * 客户端IP
	 */
	private String remoteIP;
	
	/**
	 * session是否结束
	 */
	boolean sessionEnded;
	
	/**
	 * 失去smtp连接的时间
	 */
	long connectLostTime;
	
	/**
	 * ID
	 */
	String smtpID;
	
	/**
	 * 本服务器的域名
	 */
	String[] localDomainList;
	
	/**
	 * 用于生成smtpID
	 */
	private final static Random random=new Random();
	
	/**
	 * 日记记录
	 */
	Logger logger;
	/**
	 * 登陆用户
	 */
	String userName;
	
	public SMTPSession(Socket clientSocket){
		this.clientSocket = clientSocket;
	}
	
	/**
	 * 初始化smtp服务线程
	 */
	public void initSession(){
		try {
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			in= new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			connectLostTime=Long.parseLong(EmailServerStart.emailConf.getProperty("connectLostTime"));
			localDomainList=getDomainList();
			curCommandName="";
			remoteHost=clientSocket.getInetAddress().getHostName();
			remoteIP=clientSocket.getInetAddress().getHostAddress();
			sessionEnded=false;
			smtpID=random.nextInt(1024)+"";
			logger=EmailServerStart.smtpLog;
			email=new Email();
			
			//smtp服务线程正常启动
			sendResponse("220 email server is ready");
			getLogger().info("smtp:建立一个连接"+"("+smtpID+"/"+remoteHost+"/"+remoteIP+")");
		} catch (IOException e) {
			getLogger().error("smtp:启动服务失败");
			return;
		}
	}
	
	@Override
	public void run() {
		String strInputMesg=null;
		SMTPCommandHandler ch=null;
		
		//初始化smtp session
		initSession();
		
		//循环等待命令
		while(!sessionEnded){
			//读取命令
			strInputMesg=getCommand(in, connectLostTime);
			
			//超时没有读取命令，断开连接
			if(strInputMesg==null){
				getLogger().info("smtp:服务器超时读取客户端命令");
				sessionEnded=true;
				break;
			}
			
			//处理命令
			dealCommand(strInputMesg, ch);
		}
		
		//客户端断开连接
		getLogger().info("smtp："+"ID "+smtpID+"断开连接");
		clearSession();
	}
	
	/**
	 * 处理命令
	 * @param strInputMesg
	 * @param ch
	 */
	private void dealCommand(String strInputMesg,SMTPCommandHandler ch){
		//获取命令
		String tempCommandName=null;
		String tempRemainCommandStr=null;
		int spaceIndex=strInputMesg.indexOf(" ");
		if(spaceIndex>0){
			/**
			 * 截取命令，如mail from:<111@qq.com>
			 * 截取出mail和from:<111@qq.com>
			 */
			tempCommandName=strInputMesg.substring(0, spaceIndex);
			tempRemainCommandStr=strInputMesg.substring(spaceIndex+1);
		}else {
			tempCommandName=strInputMesg;
		}
		tempCommandName=tempCommandName.toUpperCase();
		
		//调用相应的命令
		ch=SMTPDealCommand.getCommandHandler(tempCommandName);
		if(ch==null){
			//非法命令
			sendResponse("502 Command not implemented");
		}else{
			//命令存在
			curCommandName=tempCommandName;
			curRemainCommandStr=tempRemainCommandStr;
			//getLogger().info("当前命令："+curCommandName+"  上一个命令:"+lastCommandName);

			try {
				//处理命令
				ch.onCommand(this);
			} catch (SMTPException e) {
				//发送错误信息
				sendResponse(e.getErrorMsg());
			}
		}
	}
	
	/**
	 * 重置命令
	 */
	public void resetCommand(){
		curCommandName="";
		lastCommandName="";
	}

	/**
	 * 清空邮件
	 */
	public void resetState(){
		email=new Email();
	}
	
	/**
	 * 在限定时间里获取命令
	 * @param in
	 * @param timeout
	 * @return
	 */
	public String getCommand(BufferedReader in, long timeout) {
		String strInputMesg=TimeoutUtil.limitTimeReadLine(in, timeout);
		getLogger().debug("smtp读取："+strInputMesg);
		return strInputMesg;
	}

	/**
	 * 服务端回复
	 * @param out
	 * @param response
	 * @return
	 */
	public boolean sendResponse(String response) {
		out.println(response);
		out.flush();
		logger.debug("smtp回应："+response);
		return true;
	}
	
	/**
	 * 获取本服域名名称
	 * @return
	 */
	public String[] getDomainList(){
		String str=EmailServerStart.emailConf.getProperty("localDomain");
		return str.split(",");
	}
	
	/**
	 * 一个命令成功后，记录为上一个命令
	 */
	public void setLastCommandName(){
		lastCommandName=curCommandName;
	}
	
	/**
	 * 检查命令顺序是否正确
	 * @return
	 */
	public boolean checkCommandOrder(){
		if(lastCommandName==null || curCommandName==null){
			return false;
		}
		if(lastCommandName.equals("HELO") && curCommandName.equals("MAIL")){
			return true;
		}
		if(lastCommandName.equals("EHLO") && curCommandName.equals("AUTH")){
			return true;
		}
		if(lastCommandName.equals("AUTH") && curCommandName.equals("MAIL")){
			return true;
		}
		if(lastCommandName.equals("MAIL") && curCommandName.equals("RCPT")){
			return true;
		}
		if(lastCommandName.equals("RCPT") && curCommandName.equals("RCPT")){
			return true;
		}
		if(lastCommandName.equals("RCPT") && curCommandName.equals("DATA")){
			return true;
		}
		return false;
	}
	
	/**
	 * 结束smtp session
	 */
	public void clearSession(){
		if(out!=null){
			out.close();
		}
		if(in!=null){
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if(clientSocket!=null){
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

	public String getCurRemainCommandStr() {
		return curRemainCommandStr;
	}

	public Email getEmail() {
		return email;
	}

	public String getRemoteHost() {
		return remoteHost;
	}

	public String getRemoteIP() {
		return remoteIP;
	}

	public String getSmtpID() {
		return smtpID;
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

	public String getLastCommandName() {
		return lastCommandName;
	}

	public BufferedReader getIn() {
		return in;
	}

	public long getSmtpConnectLostTime() {
		return connectLostTime;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	
}
