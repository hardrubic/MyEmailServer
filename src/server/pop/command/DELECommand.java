package server.pop.command;

import java.util.List;

import server.model.Email;
import server.model.User;
import server.pop.POP3Exception;
import server.pop.POP3Session;
import server.util.EnumUtil.POP3State;

public class DELECommand implements POP3CommandHandler {

	@Override
	public void onCommand(POP3Session session, String argument1,
			String argument2) throws POP3Exception {
		// 检查会话状态和参数
		if (session.getState() != POP3State.TRANSACTION || argument1 == null
				|| argument2 != null) {
			throw new POP3Exception("-ERR syntax error");
		}

		// 检查参数是否数字
		int emailNum = -1;
		try {
			emailNum = Integer.parseInt(argument1);
		} catch (NumberFormatException e) {
			// //参数不是数字，出错
			throw new POP3Exception("-ERR syntax error");
		}

		// 检查对应邮件是否存在
		User user = session.getUser();
		List<Email> emailList = user.getEmailList();
		if (emailNum <= 0 || emailNum > user.getEmailNumber()) {
			throw new POP3Exception("-ERR no such message, only "
					+ user.getEmailNumber() + " messages in maildrop");
		}

		// 检查邮件是否被删除
		if (emailList.get(emailNum - 1).isDeleted()) {
			throw new POP3Exception("-ERR message " + emailNum
					+ " already deleted");
		} 
		
		//设置邮件状态为删除
		emailList.get(emailNum - 1).setDeleted(true);
		session.sendResponse("+OK message " + emailNum + " deleted");
	}
}
