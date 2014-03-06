package server.pop.command;

import server.pop.POP3Exception;
import server.pop.POP3Session;
import server.smtp.SMTPSession;
import server.smtp.SMTPSession;


public interface POP3CommandHandler {
	public void onCommand(POP3Session session,String argument1,String argument2) throws POP3Exception;
}
