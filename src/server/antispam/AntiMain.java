package server.antispam;

import java.util.List;

import server.model.Email;

public class AntiMain {
	private static AntiMain antiMain;
	
	private AntiMain(){
	}
	
	public static AntiMain getInstance(){
		if(antiMain==null){
			antiMain = new AntiMain();
		}
		return antiMain;
	}
	
	public boolean isSpam(Email email){
		//获取邮件正文
		List<String> emailContentList = MimeAnalysis.getEmailTextList(email);
		
		//敏感字检查
		WordSearch wordSearch=WordSearch.getInstance();
		boolean flag=false;
		for (String string : emailContentList) {
			if(wordSearch.checkVocabulary(string)){
				//发现敏感词
				flag=true;
			}
		}
		if(flag){
			return true;
		}
		return false;
	}
}
