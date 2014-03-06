package server.pop.command;

import java.util.List;

import server.model.Email;
import server.model.User;
import server.pop.POP3Exception;
import server.pop.POP3Session;
import server.util.EnumUtil.POP3State;

public class RSETCommand implements POP3CommandHandler {

	@Override
	public void onCommand(POP3Session session, String argument1,String argument2) throws POP3Exception{
		//检查会话状态
		if(session.getState()!=POP3State.TRANSACTION){
			throw new POP3Exception("-ERR auth first");
		}
		
		//检查参数数量是否合法
		if(argument1!=null || argument2!=null){
			throw new POP3Exception("-ERR syntax error");
		}
		
		//重置所有被删除的邮件
		User user=session.getUser();
		List<Email> emailList=user.getEmailList();
		for (int i = 0; i < emailList.size(); i++) {
			emailList.get(i).setDeleted(false);
		}
		session.sendResponse("+OK");
	}

}
