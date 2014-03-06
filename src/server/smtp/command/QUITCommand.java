package server.smtp.command;

import server.bin.EmailServerStart;
import server.smtp.SMTPSession;

public class QUITCommand implements SMTPCommandHandler {

	@Override
	public void onCommand(SMTPSession session) {
		session.sendResponse("221 email server closing connection");
		session.setSessionEnded(true);
	}

}
