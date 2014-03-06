package server.smtp.command;

import server.smtp.SMTPException;
import server.smtp.SMTPSession;
import server.smtp.SMTPSession;


public interface SMTPCommandHandler {
	public void onCommand(SMTPSession session) throws SMTPException;
}
