package server.smtp.command;

import it.sauronsoftware.base64.Base64;
import server.bin.UserListManager;
import server.smtp.SMTPException;
import server.smtp.SMTPSession;
import server.util.CheckUtil;
import server.util.StringUtil;

public class AUTHCommand implements SMTPCommandHandler {
	
	@Override
	public void onCommand(SMTPSession session) throws SMTPException{
		// 检查上一个命令
		if (!session.checkCommandOrder()) {
			throw new SMTPException("502 command not implemented");
		}
		
		// 检查命令
		String remainCommandStr = session.getCurRemainCommandStr();
		if (remainCommandStr==null || !remainCommandStr.toUpperCase().equals("login".toUpperCase())) {
			//命令不正确
			throw new SMTPException("500 Error:bad syntax");
		}
		
		//命令成功,等待输入账号密码
		session.sendResponse("334 "+Base64.encode("user"));
		String userNameCode=session.getCommand(session.getIn(), session.getSmtpConnectLostTime());
		String userNameDecode=Base64.decode(userNameCode);
		
		// 如果输入的是邮箱地址
		if (CheckUtil.isEmailAddress(userNameDecode)) {
			userNameDecode = StringUtil.getEmailName(userNameDecode);
		}
		
		//检查账号是否存在
		UserListManager userListManager=UserListManager.getInstance();
		if(!userListManager.checkUserExist(userNameDecode)){
			//账号不存在
			throw new SMTPException("535 authentication failed,no such user");
		}
		//检查密码
		session.sendResponse("334 "+Base64.encode("password"));
		String passwordCode=session.getCommand(session.getIn(), session.getSmtpConnectLostTime());
		String passwordDecode=Base64.decode(passwordCode);
		if(userListManager.checkUserLogin(userNameDecode, passwordDecode)){
			//通过登录
			session.setUserName(userNameDecode);
			session.sendResponse("235 OK,go ahead");
			//当前命令变为上一个命令
			session.setLastCommandName();
		}else {
			//密码不正确
			throw new SMTPException("535 authentication failed");
		}
	}

}
