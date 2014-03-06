package server.smtp.command;

import server.smtp.SMTPException;
import server.smtp.SMTPSession;

public class HELOCommand implements SMTPCommandHandler {

	@Override
	public void onCommand(SMTPSession session) throws SMTPException {
		String responseStr = null;

		// 清空缓冲区
		session.resetState();

		// 获取参数
		String argument = session.getCurRemainCommandStr();

		if (argument == null) {
			// 参数不合法
			throw new SMTPException("501 domain address required:HELO");
		} else {
			responseStr = "250 Hello " + argument;
		}
		session.sendResponse(responseStr);

		session.setLastCommandName();
	}

}
