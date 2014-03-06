package server.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import server.util.EmailUtil;

public class User implements Serializable{
	private static final long serialVersionUID = 1L;
	private String userName;
	private String password;
	transient private boolean isLockedBySMTP;
	transient private boolean isLockedByPOP3;
	private List<Email> emailList;

	public User(String user, String password) {
		this.userName = user;
		this.password = password;
		
		isLockedBySMTP=false;
		isLockedByPOP3=false;
		emailList=new ArrayList<Email>();
	}
	
	/**
	 * 获取当前用户的邮件数量
	 * @return
	 */
	public int getEmailNumber(){
		return emailList.size();
	}
	
	/**
	 * 获取邮件的总大小
	 * @return
	 */
	public long getEmailTotalSize(){
		long sum=0;
		for (int i = 0; i < emailList.size(); i++) {
			if(emailList.get(i).isDeleted()==false){
				sum+=EmailUtil.getEmailSize(emailList.get(i));
			}
		}
		return sum;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String user) {
		this.userName = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}


	public boolean isLockedBySMTP() {
		return isLockedBySMTP;
	}

	public void setLockedBySMTP(boolean isLockedBySMTP) {
		this.isLockedBySMTP = isLockedBySMTP;
	}

	public boolean isLockedByPOP3() {
		return isLockedByPOP3;
	}

	public void setLockedByPOP3(boolean isLockedByPOP3) {
		this.isLockedByPOP3 = isLockedByPOP3;
	}

	public List<Email> getEmailList() {
		return emailList;
	}

	public void setEmailList(List<Email> emailList) {
		this.emailList = emailList;
	}
}
