package server.pop;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import server.pop.command.POP3CommandHandler;
import server.util.EnumUtil.POP3State;

public class POP3DealCommand {
	
	public static POP3CommandHandler getCommandHandler(String command){
		if(command==null){
			return null;
		}
		
		//生成相应的命令类
		POP3CommandHandler ch = null;
		String commandClass=command.toUpperCase()+"Command";
		try {
			Class c=Class.forName("server.pop.command."+commandClass);
			Constructor<?> cons[]=c.getConstructors();
			ch=(POP3CommandHandler) cons[0].newInstance(null);
		} catch (ClassNotFoundException e) {
			//没有发现这个命令，不处理
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
