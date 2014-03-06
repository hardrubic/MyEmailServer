package server.smtp.command;

import server.smtp.SMTPSession;

public class NOOPCommand implements SMTPCommandHandler {

	@Override
	public void onCommand(SMTPSession session) {
		String responseStr=null;
		//获取参数
		String argument=session.getCurRemainCommandStr();
		
		if(argument!=null){
			//参数不合法
			responseStr="500 syntax error";
		}else{
			responseStr="250 OK "+argument;
		}
		session.sendResponse( responseStr);
	}

}
