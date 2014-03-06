package server.smtp.command;

import server.smtp.SMTPException;
import server.smtp.SMTPSession;

public class EHLOCommand implements SMTPCommandHandler {

	@Override
	public void onCommand(SMTPSession session) throws SMTPException{
		String responseStr = null;

		// 清空缓冲区
		session.resetState();

		// 获取参数
		String argument = session.getCurRemainCommandStr();

		if (argument == null) {
			// 参数不合法
			throw new SMTPException("500 Error:bad syntax");
		} else {
			responseStr = "250-hello "+argument;
			session.sendResponse(responseStr);
			responseStr = "250-AUTH LOGIN PLAIN";
			session.sendResponse(responseStr);
			responseStr = "250-AUTH=LOGIN PLAIN";
			session.sendResponse(responseStr);
			responseStr = "250-PIPELINING";
			session.sendResponse(responseStr);
			responseStr = "250 8BITMIME";
		}
		session.sendResponse(responseStr);

		session.setLastCommandName();
	}

}
