package server.util;

public class CheckUtil {
	/**
	 * 判断是否正确的邮箱地址
	 * @param str
	 * @return
	 */
	public static boolean isEmailAddress(String str){
		if(str.matches("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$")){
			return true;
		}
		return false;
	}
	
	/**
	 * 判断是否符合smtp定义中的<path>格式
	 * 未完善
	 * @param str
	 * @return
	 */
	public static boolean isCorrectPath(String str){
		str=str.trim();
		if(str.startsWith("<") && str.endsWith(">")){
			str=str.substring(1, str.length()-1);
			if(isEmailAddress(str)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 判断域名在不在一个列表中
	 * @param domain
	 * @param domainList
	 * @return
	 */
	public static boolean isSameDomain(String domain,String[] domainList){
		for (int i = 0; i < domainList.length; i++) {
			if(domain.equals(domainList[i])){
				return true;
			}
		}
		return false;
	}
}
