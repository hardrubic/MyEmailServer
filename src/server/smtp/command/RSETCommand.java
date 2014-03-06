package server.smtp.command;

import server.smtp.SMTPException;
import server.smtp.SMTPSession;

public class RSETCommand implements SMTPCommandHandler {

	@Override
	public void onCommand(SMTPSession session) throws SMTPException{
		String argument = session.getCurRemainCommandStr();
		if (argument != null) {
			// 带参数，错误
			throw new SMTPException("500 unexpected argument");
		}
		session.sendResponse("250 OK");
		//重置参数
		session.resetState();
		session.resetCommand();
	}

}
