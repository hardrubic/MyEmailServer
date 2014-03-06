package server.pop.command;

import java.util.List;

import server.model.Email;
import server.model.User;
import server.pop.POP3Exception;
import server.pop.POP3Session;
import server.util.EnumUtil.POP3State;

public class UIDLCommand implements POP3CommandHandler {

	@Override
	public void onCommand(POP3Session session, String argument1,
			String argument2) throws POP3Exception {
		// 检查会话状态
		if (session.getState() != POP3State.TRANSACTION) {
			throw new POP3Exception("-ERR auth first");
		}

		// 参数数量不同对应不同操作
		if (argument1 == null && argument2 == null) { // 没有参数，列出所有邮件的uidl
			dealOneArgument(session);
		} else if (argument1 != null && argument2 == null) {
			// 检查参数是否是数字
			int emailNum = -1;
			try {
				emailNum = Integer.parseInt(argument1);
			} catch (NumberFormatException e) {
				// 参数不是数字，出错
				throw new POP3Exception("-ERR syntax error");
			}
			dealTwoArgument(session, emailNum);
		} else {
			// 错误参数
			throw new POP3Exception("-ERR syntax error");
		}
	}

	private void dealOneArgument(POP3Session session) {
		User tempUser = session.getUser();
		List<Email> tempList = tempUser.getEmailList();
		session.sendResponse("+OK");

		for (int i = 0; i < tempList.size(); i++) {
			session.sendResponse(i + 1 + " " + tempList.get(i).getUidl());
		}
		session.sendResponse(".");
	}

	private void dealTwoArgument(POP3Session session, int emailNum)
			throws POP3Exception {
		User tempUser = session.getUser();
		// 列出某一个邮件的uidl
		if (emailNum <= 0 || emailNum > tempUser.getEmailNumber()) {
			throw new POP3Exception("-ERR no such message, only "
					+ tempUser.getEmailNumber() + " messages in maildrop");
		} else {
			Email email = session.getUser().getEmailList().get(emailNum - 1);
			session.sendResponse("+OK " + emailNum + " " + email.getUidl());
		}
	}

}
