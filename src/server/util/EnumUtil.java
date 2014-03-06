package server.util;

public class EnumUtil {
	/**
	 * 账户锁定类型
	 * @author heng
	 *
	 */
	public enum LockType{
		SMTP,POP3,
	}
	
	/**
	 * pop3状态
	 * @author heng
	 *
	 */
	public enum POP3State{
		AUTHORIZATION_READY,AUTHORIZATION_USERSET,TRANSACTION
	}
}
