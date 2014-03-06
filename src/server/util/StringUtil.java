package server.util;

public class StringUtil {

	/**
	 * 获取邮件地址的名称
	 * @param emailAddress
	 * @return
	 */
	public static String getEmailName(String emailAddress){
		int index=emailAddress.indexOf("@");
		return emailAddress.substring(0, index);
	}
	
	/**
	 * 获取邮件地址的域名
	 * @param emailAddress
	 * @return
	 */
	public static String getEmailDomain(String emailAddress){
		int index=emailAddress.indexOf("@");
		return emailAddress.substring(index+1,emailAddress.length());
	}
	
	/**
	 * 从形如  
	 * heng <heng@sun.com>,
	 * 中截取电子邮件地址
	 * @param str
	 * @return
	 */
	public static String getEmailFromContent(String str){
		int startIndex=str.indexOf('<');
		int endIndex=str.indexOf(">");
		String s=str.substring(startIndex+1,endIndex);
		return s;
	}
	
	/**
	 * 从形如
	 * from:<heng@sun.com>
	 * 获取冒号前的字段
	 * @param remainCommandStr
	 * @return
	 */
	public static String getRemainCommand(String remainCommandStr){
		String remainCommand=null;
		if ((remainCommandStr != null) && (remainCommandStr.indexOf(":") > 0)) {
			int colonIndex = remainCommandStr.indexOf(":");
			remainCommand = remainCommandStr.substring(0, colonIndex).trim();
		}
		return remainCommand;
	}
	
	/**
	 * 从形如
	 * from:<heng@sun.com>
	 * 获取冒号后的字段
	 * @param remainCommandStr
	 * @return
	 */
	public static String getAddressFromArgument(String remainCommandStr){
		// 获取参数
		String address = null;

		if ((remainCommandStr != null) && (remainCommandStr.indexOf(":") > 0)) {
			int colonIndex = remainCommandStr.indexOf(":");
			address = remainCommandStr.substring(colonIndex + 1).trim();
		}
		return address;
	}
}
