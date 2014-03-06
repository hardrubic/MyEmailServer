package server.pop.command;

import java.util.List;

import server.model.Email;
import server.model.User;
import server.pop.POP3Exception;
import server.pop.POP3Session;
import server.util.EnumUtil.POP3State;

public class TOPCommand implements POP3CommandHandler {

	@Override
	public void onCommand(POP3Session session, String argument1,
			String argument2) throws POP3Exception {
		//检查会话状态是否合法
		if (session.getState() != POP3State.TRANSACTION) {
			throw new POP3Exception("-ERR auth first");
		}

		//不同参数数量对应不同处理方法
		try {
			if (argument1 != null && argument2 == null) {	// 一个参数，列出某个邮件全部正文
				int emailNum = Integer.parseInt(argument1);
				dealOneArgument(session,emailNum);
			} else if (argument1 != null && argument2 != null) {		// 列出一个邮件的若干行正文
				int emailNum = Integer.parseInt(argument1);
				int lineNum = Integer.parseInt(argument2);
				dealTwoArgument(session, emailNum, lineNum);
			} else {
				// 错误参数
				throw new POP3Exception("-ERR syntax error");
			}
		} catch (NumberFormatException e) {
			// 参数不是数字，出错
			throw new POP3Exception("-ERR syntax error");
		}
	}
	
	/**
	 * 处理一个参数的情况
	 * @param session
	 * @param emailList
	 * @param emailNum
	 * @throws POP3Exception
	 */
	private void dealOneArgument(POP3Session session,int emailNum) throws POP3Exception{
		User user = session.getUser();
		List<Email> emailList = user.getEmailList();
		//检查邮件标号是否合法
		if (emailNum <= 0 || emailNum > user.getEmailNumber()) {
			throw new POP3Exception("-ERR no such message, only "
					+ user.getEmailNumber()
					+ " messages in maildrop");
		} else {
			Email email = emailList.get(emailNum - 1);
			if (email.isDeleted() == true) {
				throw new POP3Exception("-ERR message already deleted");
			} else {
				// 显示整份邮件
				session.sendResponse("+OK");
				// 发送邮件正文
				String content = email.getContent();
				sendEmailContent(content, session);
			}
		}
	}
	
	private void dealTwoArgument(POP3Session session,int emailNum,int lineNum) throws POP3Exception{
		User user = session.getUser();
		List<Email> emailList = user.getEmailList();
		//检查邮件标号和行数是否合法
		if (emailNum <= 0 || emailNum > user.getEmailNumber()) {
			throw new POP3Exception("-ERR no such message, only "
					+ user.getEmailNumber()
					+ " messages in maildrop");
		} else if (lineNum < 0) {
			throw new POP3Exception("-ERR syntax error");
		} 
		
		//检查邮件是否被删除
		Email email = emailList.get(emailNum - 1);
		if (email.isDeleted() == true) {
			throw new POP3Exception("-ERR message already deleted");
		} 
		
		boolean isHeaderFound = false;
		int countLine = lineNum;
		session.sendResponse("+OK");

		// output email header
		String content = email.getContent();
		String temp[] = content.split("\n");
		for (int i = 0; i < temp.length; i++) {
			// 未结束header
			if (isHeaderFound == false) {
				session.sendResponse(temp[i]);
			}
			// header已输出
			if (isHeaderFound == true) {
				if (lineNum == 0) {
					session.sendResponse(temp[i]);
					break;
				}
				if (countLine > 0) {
					session.sendResponse(temp[i]);
					countLine--;
				}
			}
			if (temp[i].trim().length() == 0) {
				isHeaderFound = true;
			}
		}
		session.sendResponse(".");
	}
	
	/**
	 * 向客户端发送邮件正文
	 * @param content
	 * @param session
	 */
	private void sendEmailContent(String content,POP3Session session){
		String temp[] = content.split("\n");
		for (String t : temp) {
			session.sendResponse(t);
		}
		session.sendResponse(".");
	}

}
