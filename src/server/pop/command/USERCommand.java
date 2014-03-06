package server.pop.command;

import server.pop.POP3Exception;
import server.pop.POP3Session;
import server.util.CheckUtil;
import server.util.StringUtil;
import server.util.EnumUtil.POP3State;

public class USERCommand implements POP3CommandHandler {

	@Override
	public void onCommand(POP3Session session, String argument1,
			String argument2) throws POP3Exception {
		//检查会话状态和参数是否合法
		if (session.getState() != POP3State.AUTHORIZATION_READY
				|| argument1 == null || argument2 != null) {
			throw new POP3Exception("-ERR syntax error");
		}
		
		// 如果输入的是邮箱地址
		String userName=argument1;
		if (CheckUtil.isEmailAddress(argument1)) {
			userName = StringUtil.getEmailName(argument1);
		}
		// 标记有用户登陆
		session.setLoginUserName(userName);
		session.setState(POP3State.AUTHORIZATION_USERSET);
		session.getLogger().debug("pop3:" + userName + " try to login");
		session.sendResponse("+OK");
	}

}
