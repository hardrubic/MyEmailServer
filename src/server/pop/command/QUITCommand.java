package server.pop.command;

import java.util.List;

import server.bin.UserListManager;

import server.model.Email;
import server.model.User;
import server.pop.POP3Exception;
import server.pop.POP3Session;
import server.util.EnumUtil.LockType;
import server.util.EnumUtil.POP3State;

public class QUITCommand implements POP3CommandHandler {

	@Override
	public void onCommand(POP3Session session, String argument1,
			String argument2) throws POP3Exception {
		//检查参数是否合法
		if (argument1 != null || argument2 != null) {
			throw new POP3Exception("-ERR syntax error");
		}
		
		// 进入updata模式
		if (session.getState() == POP3State.TRANSACTION) {
			User user = session.getUser();
			// 删除标记的邮件
			deleteMarkEmail(user);
			// 保存用户列表
			UserListManager userListManager = UserListManager.getInstance();
			userListManager.writeUserList();
			// 解锁用户
			userListManager.unlockUser(user.getUserName(), LockType.POP3);
		}
		
		// 正常退出 server
		session.sendResponse("+OK bye");
		session.setSessionEnded(true);
	}
	
	/**
	 * 删除被标记的邮件
	 * @param user
	 */
	private void deleteMarkEmail(User user){
		List<Email> emailList = user.getEmailList();
		boolean flag = true;
		while (flag) {
			flag = false;
			for (int i = 0; i < emailList.size(); i++) {
				if (emailList.get(i).isDeleted() == true) {
					emailList.remove(i);
					flag = true;
					break;
				}
			}
		}
	}
}
