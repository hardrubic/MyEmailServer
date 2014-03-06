package server.pop.command;

import server.bin.UserListManager;
import server.pop.POP3Exception;
import server.pop.POP3Session;
import server.util.EnumUtil.LockType;
import server.util.EnumUtil.POP3State;

public class PASSCommand implements POP3CommandHandler {

	@Override
	public void onCommand(POP3Session session, String argument1,
			String argument2) throws POP3Exception {
		//检查会话状态和参数是否合法
		if (session.getState() != POP3State.AUTHORIZATION_USERSET
				|| argument1 == null || argument2 != null) {
			throw new POP3Exception("-ERR syntax error");
		}
		// 取得登陆账号和密码
		String loginUserName = session.getLoginUserName();
		String password = argument1;

		UserListManager userListManager = UserListManager.getInstance();
		// 检查账号和密码
		if (!userListManager.checkUserLogin(loginUserName, password)) {
			session.setState(POP3State.AUTHORIZATION_READY);
			throw new POP3Exception("-ERR authentication failed");
		}
		
		// 账号密码正确，接着检查用户是否已经登陆
		if (!userListManager.isUserLocked(loginUserName, LockType.POP3)) {
			// 登陆成功，锁定用户
			userListManager.lockUser(loginUserName, LockType.POP3);
			// 创建用户实例
			session.setUser(userListManager.getUser(loginUserName));
			//更改会话状态
			session.setState(POP3State.TRANSACTION);
			session.sendResponse("+OK welcome " + loginUserName);

			session.getLogger().debug(
					"pop3:" + loginUserName + " login successfully");
		} else {
			//登陆失败，回滚会话状态
			session.setState(POP3State.AUTHORIZATION_READY);
			throw new POP3Exception("-ERR user has logined");
		}
	}

}
