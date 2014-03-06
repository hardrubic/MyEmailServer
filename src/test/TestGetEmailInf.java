package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import server.bin.EmailServerStart;
import server.model.Email;
import server.smtp.SMTPDealCommand;
import server.smtp.command.SMTPCommandHandler;
import server.util.*;

public class TestGetEmailInf {
	public static void main(String[] args) {
		File file = new File("src/temp/content1.txt");
		StringBuffer content=new StringBuffer();
		BufferedReader br = null;
		try {
			br=new BufferedReader(new FileReader(file));
			String s=null;
			while((s=br.readLine())!=null){
				content.append(s);
				content.append("\n");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
//		String t= getCc(content.toString());
//		List<String> toList=getCc(content.toString());
//		for (int i = 0; i < toList.size(); i++) {
//			System.out.println(toList.get(i));
//		}
		getFrom(content.toString());
	}
	
	public static String getFrom(String content){
		String header=getHeader(content);
		String headerLine[]=header.split("\n");
		String from=null;
		for (int i = 0; i < headerLine.length; i++) {
			headerLine[i]=headerLine[i].trim();
			if(headerLine[i].startsWith("From")){
				String headerLineContent[]=headerLine[i].split(":",2);
				from=StringUtil.getEmailFromContent(headerLineContent[1]);
				break;
			}
		}		
		System.out.println(from);
		return null;
	}
	
	public static String getHeader(String content){
		String str[]=content.split("\n\n",2);
		return str[0];
	}
	
	public static ArrayList<String> getTo(String content){
		String header=getHeader(content);
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
						String email=aloneEmail[j];
						if(aloneEmail[j].contains("<") && aloneEmail[j].contains(">")){
							//去掉尖括号
							email=StringUtil.getEmailFromContent(aloneEmail[j]);
						}
						toList.add(email.trim());
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
	
	public static String getSubject(String content){
		String header=getHeader(content);
		String str[]=header.split("\n");
		for (int i = 0; i < str.length; i++) {
			if(str[i].startsWith("Subject")){
				String str2[]=str[i].split(":");
				return str2[1].trim();
			}
		}
		return null;
	}
	
	public static String getDate(String content){
		String header=getHeader(content);
		String str[]=header.split("\n");
		for (int i = 0; i < str.length; i++) {
			if(str[i].startsWith("Date")){
				String str2[]=str[i].split(":",2);
				return str2[1].trim();
			}
		}
		return null;
	}
	
	public static ArrayList<String> getCc(String content){
		String header=getHeader(content);
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
						String email=aloneEmail[j];
						if(aloneEmail[j].contains("<") && aloneEmail[j].contains(">")){
							//去掉尖括号
							email=StringUtil.getEmailFromContent(aloneEmail[j]);
						}
						ccList.add(email.trim());
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
}
