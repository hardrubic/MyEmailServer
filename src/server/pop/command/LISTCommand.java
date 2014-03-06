package server.pop.command;

import java.util.List;

import server.bin.UserListManager;

import server.model.Email;
import server.model.User;
import server.pop.POP3Exception;
import server.pop.POP3Session;
import server.util.EmailUtil;
import server.util.EnumUtil.POP3State;

public class LISTCommand implements POP3CommandHandler {

	@Override
	public void onCommand(POP3Session session, String argument1,
			String argument2) throws POP3Exception {
		// 检查会话状态是否合法
		if (session.getState() != POP3State.TRANSACTION) {
			throw new POP3Exception("-ERR auth first");
		}

		User user = session.getUser();
		List<Email> emailList = user.getEmailList();
		/*
		 * 不同参数有不同操作
		 */
		if (argument1 == null && argument2 == null) { // 没有参数，列出所有邮件的信息
			// 列出总体情况
			session.sendResponse("+OK " + user.getEmailNumber() + " messages("
					+ user.getEmailTotalSize() + " octets)");
			// 列出每一封邮件情况
			listAllEmail(emailList, session);
			// 结束列出
			session.sendResponse(".");
		} else if (argument1 != null && argument2 == null) { // 列出某一个邮件的信息
			//检查参数是否数字
			int emailNum = -1;
			try {
				emailNum = Integer.parseInt(argument1);
			} catch (NumberFormatException e) {
				// 参数不是数字，出错
				throw new POP3Exception("-ERR syntax error");
			}
			
			//检查该标号的邮件是否存在
			if (emailNum <= 0 || emailNum > user.getEmailNumber()) {
				throw new POP3Exception("-ERR no such message, only "
						+ user.getEmailNumber() + " messages in maildrop");
			} 
			
			//获取邮件
			Email email = session.getUser().getEmailList()
					.get(emailNum - 1);
			//检查邮件是否被删除
			if (email.isDeleted() == true) {
				throw new POP3Exception("-ERR message already deleted");
			} 
			//列出邮件信息
			session.sendResponse("+OK " + emailNum + " "
						+ EmailUtil.getEmailSize(email));
		} else {
			// 错误参数
			throw new POP3Exception("-ERR syntax error");
		}
	}

	/**
	 * 列出所有邮件的情况
	 * 
	 * @param emailList
	 */
	private void listAllEmail(List<Email> emailList, POP3Session session) {
		String responseStr = null;
		for (int i = 0; i < emailList.size(); i++) {
			if (emailList.get(i).isDeleted() == false) {
				responseStr = i + 1 + " "
						+ EmailUtil.getEmailSize(emailList.get(i));
				session.sendResponse(responseStr);
			}
		}
	}
}
