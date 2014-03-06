package server.pop.command;

import java.util.List;

import server.model.Email;
import server.model.User;

import server.pop.POP3Exception;
import server.pop.POP3Session;
import server.util.EnumUtil.POP3State;

public class STATCommand implements POP3CommandHandler {

	@Override
	public void onCommand(POP3Session session, String argument1,
			String argument2) throws POP3Exception {
		//检查会话状态
		if (session.getState() != POP3State.TRANSACTION) {
			throw new POP3Exception("-ERR auth first");
		}
		
		//检查参数
		if (argument1 == null && argument2 == null) {
			// 统计邮件数量和大小
			User user = session.getUser();
			session.sendResponse("+OK " + user.getEmailNumber() + " "
					+ user.getEmailTotalSize());
		} else {
			throw new POP3Exception("-ERR syntax error");
		}
	}
}
