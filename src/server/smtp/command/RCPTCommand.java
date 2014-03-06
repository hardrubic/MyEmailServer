package server.smtp.command;

import server.smtp.SMTPException;
import server.smtp.SMTPSession;
import server.util.CheckUtil;
import server.util.DebugUtil;
import server.util.StringUtil;

public class RCPTCommand implements SMTPCommandHandler {
	
	@Override
	public void onCommand(SMTPSession session) throws SMTPException{
		// 检查上一个命令
		if (!session.checkCommandOrder()) {
			throw new SMTPException("502 command not implemented");
		}
			
		// 获取参数
		String remainCommand = StringUtil.getRemainCommand(session.getCurRemainCommandStr());
		String receiveAddress=StringUtil.getAddressFromArgument(session.getCurRemainCommandStr());

		// 检查命令是否正确
		if (remainCommand == null || !remainCommand.toUpperCase().equals("TO")
				|| receiveAddress == null) {
			throw new SMTPException("501 Syntax error in parameters or arguments");
		}
		// 检查回复地址是否合法
		if (!CheckUtil.isCorrectPath(receiveAddress)) {
			throw new SMTPException("501 Syntax error in parameters or arguments");
		}
		// 去除尖括号
		receiveAddress = receiveAddress.substring(1, receiveAddress.length() - 1);

		// 获取地址信息
		String emailName = StringUtil.getEmailName(receiveAddress);
		String emailDomain = StringUtil.getEmailDomain(receiveAddress);

		//检测收件人地址是否有效
		
		// 记录发件人
		session.getEmail().getRecipientsList().add(receiveAddress);
		
		// 命令完结
		session.setLastCommandName();
		session.sendResponse("250 OK");
	}
}
