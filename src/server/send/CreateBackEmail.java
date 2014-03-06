package server.send;

import java.util.Date;
import java.util.List;

import org.apache.james.mime4j.dom.address.Address;
import org.apache.james.mime4j.field.address.AddressBuilder;
import org.apache.james.mime4j.field.address.ParseException;
import org.apache.james.mime4j.message.MessageImpl;

import server.bin.EmailServerStart;
import server.model.Email;
import server.util.EmailUtil;

/**
 * 创建回退邮件
 * @author heng
 *
 */
public class CreateBackEmail {
	public static Email getBackEmail(Email email){
		List<String> unsendAddress=email.getUnsendList();
		List<String> sentAddress=email.getSentAddress();
		Email backEmail=new Email();
		
		StringBuffer content = new StringBuffer();
		//header
		MessageImpl message = new MessageImpl();
		message.setDate(new Date());
		try {
			String localHostName=EmailServerStart.emailConf.getProperty("localHostName");
			message.setFrom(AddressBuilder.DEFAULT.parseMailbox(localHostName+" <"+localHostName+"@sun.com>"));
			Address toAddress = AddressBuilder.DEFAULT.parseMailbox("<"+EmailUtil.getFrom(email)+">");
			message.setTo(toAddress);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		message.createMessageId("sun.com");
		message.setSubject("system back");
		
		content.append("Date:"+message.getDate()+"\n")
		.append("From:"+message.getFrom().get(0)+"\n")
		.append("To:"+message.getTo().get(0)+"\n")
		.append("Reply-To:"+message.getFrom().get(0)+"\n")
		.append("Subject:"+message.getSubject()+"\n")
		.append("Mime-Version: 1.0"+"\n")
		.append("Message-ID:"+message.getMessageId()+"\n")
		.append("Content-Type:"+message.getMimeType()+"\n")
		.append("\n");
		for (int i = 0; i < sentAddress.size(); i++) {
			content.append("delivered to "+sentAddress.get(i)+"\n");
		}
		content.append("\n");
		for (int i = 0; i < unsendAddress.size(); i++) {
			content.append("can't deliver to "+unsendAddress.get(i)+"\n");
		}
		
		backEmail.setContent(content.toString());
		
//		System.out.println(backEmail.getContent());
		
		return backEmail;
	}
	
}
