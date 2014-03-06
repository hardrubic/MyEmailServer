package server.pop.command;

import server.pop.POP3Exception;
import server.pop.POP3Session;

public class NOOPCommand implements POP3CommandHandler {

	@Override
	public void onCommand(POP3Session session, String argument1,
			String argument2) throws POP3Exception{
		if (argument1 == null && argument2 == null) {
			session.sendResponse("+OK");
		} else {
			throw new POP3Exception("-ERR syntax error");
		}
	}

}
