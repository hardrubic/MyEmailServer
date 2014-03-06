package server.pop.command;

import server.model.Email;
import server.model.User;
import server.pop.POP3Exception;
import server.pop.POP3Session;
import server.util.EmailUtil;
import server.util.EnumUtil.POP3State;

public class RETRCommand implements POP3CommandHandler {

	@Override
	public void onCommand(POP3Session session, String argument1,
			String argument2) throws POP3Exception {
		// 检查会话是否合法
		if (session.getState() != POP3State.TRANSACTION) {
			throw new POP3Exception("-ERR auth first");
		}

		// 检查参数数量是否合法
		if (argument1 == null || argument2 != null) {
			throw new POP3Exception("-ERR syntax error");
		}
		
		//检查参数是否是数字
		int emailNum=-1;
		try {
			emailNum = Integer.parseInt(argument1);
		} catch (NumberFormatException e) {
			// 参数不是数字，出错
			throw new POP3Exception("-ERR syntax error");
		}
		
		User user = session.getUser();
		//检查邮件标志是否存在
		if (emailNum <= 0 || emailNum > user.getEmailNumber()) {
			throw new POP3Exception( "-ERR no such message, only "
					+ user.getEmailNumber() + " messages in maildrop");
		}
		
		//检查邮件是否被删除
		Email email = session.getUser().getEmailList()
				.get(emailNum - 1);
		if (email.isDeleted() == true) {
			throw new POP3Exception("-ERR message already deleted");
		} 
		
		// 发送邮件正文
		session.sendResponse("+OK " + EmailUtil.getEmailSize(email)
				+ " octets");
		String content = email.getContent();
		String temp[] = content.split("\n");
		for (String t : temp) {
			session.sendResponse(t);
		}
		session.sendResponse(".");
	}
}
