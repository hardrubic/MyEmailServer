package server.smtp.command;

import server.smtp.SMTPException;
import server.smtp.SMTPSession;
import server.util.CheckUtil;
import server.util.DebugUtil;
import server.util.StringUtil;

public class MAILCommand implements SMTPCommandHandler {
	private String emailName;
	private String emailDomain;
	private String responseStr;

	@Override
	public void onCommand(SMTPSession session) throws SMTPException{
		// 检查命令顺序是否合法
		if(!session.checkCommandOrder()){
			throw new SMTPException("503 send HELO or ehlo first");
		}
		// 清空缓冲区
		session.resetState();
		
		//获取邮件地址
		String senderAddress=StringUtil.getAddressFromArgument(session.getCurRemainCommandStr());
		String remainCommand=StringUtil.getRemainCommand(session.getCurRemainCommandStr());

		// 检查命令是否正确
		if (remainCommand == null || !remainCommand.toUpperCase().equals("FROM")|| senderAddress == null) {
			throw new SMTPException("501 Syntax error in parameters or arguments");
		}
		// 检查回复地址是否合法
		if (!CheckUtil.isCorrectPath(senderAddress)) {
			throw new SMTPException("501 Syntax error in parameters or arguments");
		}
		// 去除尖括号
		senderAddress = senderAddress.substring(1, senderAddress.length() - 1);

		// 获取地址信息
		emailName = StringUtil.getEmailName(senderAddress);
		emailDomain = StringUtil.getEmailDomain(senderAddress);

		//检查是否登陆用户作为发件人
		if(!emailName.equals(session.getUserName())){
			throw new SMTPException("553 mail from must equal authorized user");
		}
		
		// 检查发件人和服务器是不是在同一个域
		if (CheckUtil.isSameDomain(emailDomain, session.getDomainList())) {
			// 在同一个域
//				session.getLogger().info("发件人与服务器在同一个域，可以发邮件");
			
			//命令完结
			session.setLastCommandName();
			responseStr="250 Sender <"+senderAddress+"> OK";
			session.sendResponse( responseStr);
		} else {
			// 不同一个域
			session.getLogger().info("发件人与服务器在不同一个域");
			throw new SMTPException("553 mail from must equal authorized user");
		}
	}
}
