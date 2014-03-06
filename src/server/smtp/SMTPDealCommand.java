package server.smtp;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import server.smtp.command.SMTPCommandHandler;

public class SMTPDealCommand {
	
	public static SMTPCommandHandler getCommandHandler(String command){
		if(command==null){
			return null;
		}
		
		//生成相应的命令类
		SMTPCommandHandler ch = null;
		String commandClass=command.toUpperCase()+"Command";
		try {
			Class c=Class.forName("server.smtp.command."+commandClass);
			Constructor<?> cons[]=c.getConstructors();
			ch=(SMTPCommandHandler) cons[0].newInstance(null);
		} catch (ClassNotFoundException e) {
			//没有发现命令，不处理
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return ch;
	}
}
