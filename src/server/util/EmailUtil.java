package server.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import server.model.Email;

public class EmailUtil {
	/**
	 * 获取邮件大小
	 * @param email
	 * @return
	 */
	public static long getEmailSize(Email email){
		return email.getContent().length();
	}
	
	/**
	 * 从context获取邮件头
	 * @return
	 */
	public static String getHeader(Email email){
		String str[]=email.getContent().split("\n\n", 2);
		return str[0];
	}
	
	/**
	 * 从邮件原文获取邮件头的某一个属性
	 * @param email
	 * @param attributeName
	 * @return
	 */
	private static String getHeaderAttribute(Email email,String attributeName){
		String header=getHeader(email);
		String headerLine[]=header.split("\n");
		for (int i = 0; i < headerLine.length; i++) {
			headerLine[i]=headerLine[i].trim();
			if(headerLine[i].startsWith(attributeName)){
				String headerLineContent[]=headerLine[i].split(":",2);
				return headerLineContent[1];
			}
		}	
		return null;
	}
	
	/**
	 * 获取发件人
	 * @return
	 */
	public static String getFrom(Email email){
		String value=getHeaderAttribute(email, "From");
		return StringUtil.getEmailFromContent(value);
	}
	
	/**
	 * 获取邮件主题
	 * @return
	 */
	public static String getSubject(Email email){
		return getHeaderAttribute(email, "Subject");
	}
	
	
	/**
	 * 获取邮件时间
	 * @return
	 */
	public static String getDate(Email email){
		return getHeaderAttribute(email, "Date");
	}
	
	/**
	 * 从context获取发送地址
	 * @return
	 */
	public static ArrayList<String> getTo(Email email){
		String header=getHeader(email);
		String headerLine[]=header.split("\n");
		ArrayList<String> toList=new ArrayList<String>();
		boolean findToMuti=false;
		for (int i = 0; i < headerLine.length; i++) {
			headerLine[i]=headerLine[i].trim();
			if(headerLine[i].startsWith("To")){
				String headerLineContent[]=headerLine[i].split(":",2);
				if(headerLineContent[1].trim().endsWith(",")){
					//foxmail多行
					findToMuti=true;
					//读取带To的第一行
					toList.add(StringUtil.getEmailFromContent(headerLineContent[1]));
				}else {
					//单行
					String aloneEmail[]=headerLineContent[1].trim().split(",");
					for (int j = 0; j < aloneEmail.length; j++) {
						String emailAddress=aloneEmail[j];
						if(aloneEmail[j].contains("<") && aloneEmail[j].contains(">")){
							//去掉尖括号
							emailAddress=StringUtil.getEmailFromContent(aloneEmail[j]);
						}
						toList.add(emailAddress.trim());
					}
					break;
				}
			}else if(!headerLine[i].startsWith("To") && findToMuti==true){
				//继续读取to,判断是不是最后一行收件人
				if(!headerLine[i].endsWith(",")){
					//已经是最后一行
					findToMuti=false;
				}
				toList.add(StringUtil.getEmailFromContent(headerLine[i]));
				if(findToMuti==false){
					break;
				}
			}
		}
		return toList;
	}
	
	/**
	 * 从context获取抄送地址
	 * @return
	 */
	public static ArrayList<String> getCc(Email email){
		String header=getHeader(email);
		String headerLine[]=header.split("\n");
		ArrayList<String> ccList=new ArrayList<String>();
		boolean findCcMuti=false;
		for (int i = 0; i < headerLine.length; i++) {
			headerLine[i]=headerLine[i].trim();
			if(headerLine[i].startsWith("Cc")){
				String headerLineContent[]=headerLine[i].split(":",2);
				if(headerLineContent[1].trim().endsWith(",")){
					//foxmail多行
					findCcMuti=true;
					//读取带Cc的第一行
					ccList.add(StringUtil.getEmailFromContent(headerLineContent[1]));
				}else {
					//单行
					String aloneEmail[]=headerLineContent[1].trim().split(",");
					for (int j = 0; j < aloneEmail.length; j++) {
						String emailAddress=aloneEmail[j];
						if(aloneEmail[j].contains("<") && aloneEmail[j].contains(">")){
							//去掉尖括号
							emailAddress=StringUtil.getEmailFromContent(aloneEmail[j]);
						}
						ccList.add(emailAddress.trim());
					}
					break;
				}
			}else if(!headerLine[i].startsWith("Cc") && findCcMuti==true){
				//继续读取cc,判断是不是最后一行收件人
				if(!headerLine[i].endsWith(",")){
					//已经是最后一行
					findCcMuti=false;
				}
				ccList.add(StringUtil.getEmailFromContent(headerLine[i]));
				if(findCcMuti==false){
					break;
				}
			}
		}
		return ccList;
	}
	
	/**
	 * 获取暗送人地址
	 * @return
	 */
	public static ArrayList<String> getBcc(Email email){
		Set<String> list=new HashSet<String>();
		list.addAll(getTo(email));
		list.addAll(getCc(email));
		
		Set<String> bcc=email.getRecipientsList();
		bcc.removeAll(list);
		return new ArrayList<String>(bcc);
	}
	
	/**
	 * 获取所有收件人
	 * @return
	 */
	public static ArrayList<String> getAllRecipients(Email email){
		ArrayList<String> recipientsList = new ArrayList<String>();
		recipientsList.addAll(getTo(email));
		recipientsList.addAll(getCc(email));
		recipientsList.addAll(getBcc(email));
		return recipientsList;
	}
	
	/**
	 * 获取邮件正文
	 * @return
	 */
	public static String getText(Email email){
		String str[]=email.getContent().split("\n\n", 2);
		return str[1];
	}
}
