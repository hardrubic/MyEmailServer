package server.antispam;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import org.apache.james.mime4j.MimeException;
import org.apache.james.mime4j.dom.BinaryBody;
import org.apache.james.mime4j.dom.Body;
import org.apache.james.mime4j.dom.Entity;
import org.apache.james.mime4j.dom.Header;
import org.apache.james.mime4j.dom.Message;
import org.apache.james.mime4j.dom.MessageBuilder;
import org.apache.james.mime4j.dom.Multipart;
import org.apache.james.mime4j.dom.TextBody;
import org.apache.james.mime4j.message.DefaultMessageBuilder;
import org.apache.james.mime4j.message.MessageImpl;
import org.apache.james.mime4j.stream.Field;

import server.model.Email;

public class MimeAnalysis {
	private static List<String> textBodyList;
//	private List<String> binaryBodyList = new ArrayList<String>();
	
	public static void main(String[] args) throws MimeException, IOException {
		MimeAnalysis ma=new MimeAnalysis();
		List<String> list = ma.getEmailTextList(null);
		for (int i = 0; i < list.size(); i++) {
			System.out.println(list.get(i));
		}
	}
	
	public static List<String> getEmailTextList(Email email){
		ByteArrayInputStream stream = new ByteArrayInputStream(email.getContent().getBytes());
		
		final MessageBuilder builder = new DefaultMessageBuilder();
		Message message = null;
		textBodyList=new ArrayList<String>();
		try {
			message = (MessageImpl) builder.parseMessage(stream);
//			message = (MessageImpl) builder.parseMessage(new FileInputStream("src/server/temp/content1.txt"));
		} catch (MimeException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Header rootHeader = message.getHeader();
		createNode(message);
		return textBodyList;
	}

	private static void createNode(Header header) {
//		System.out.println("Header——————————————————————————");

		for (Field field : header.getFields()) {
			String name = field.getName();
//			System.out.println(name + ":" + field.getBody());
		}
//		System.out.println("HeaderEnd————————————————————————");
//		System.out.println();
	}

	private static void createNode(Multipart multipart) {
//		System.out.println("Multipart-----------------------");

		if(multipart.getPreamble()!=null){
//			System.out.println("Preamble:" + multipart.getPreamble());
		}
		for (Entity part : multipart.getBodyParts()) {
			createNode(part);
		}
		if (multipart.getEpilogue() != null) {
//			System.out.println("Epilogue:" + multipart.getEpilogue());
		}
//		System.out.println("MultipartEnd----------------------");
//		System.out.println();
	}

	private static void createNode(Entity entity) {
		createNode(entity.getHeader());

		Body body = entity.getBody();

		if (body instanceof Multipart) {
			/*
			 * The body of the entity is a Multipart.
			 */
			createNode((Multipart) body);
		} else if (body instanceof MessageImpl) {
			/*
			 * The body is another Message.
			 */
			createNode((MessageImpl) body);
		} else {
			/*
			 * Discrete Body (either of type TextBody or BinaryBody).
			 */
			String type=null;
			StringBuffer content=null;
			String temp=null;
//			System.out.println("````````````````");
			if (body instanceof BinaryBody) {
				type = "Binary body";
				type += " (" + entity.getMimeType() + ")";
//				System.out.println(type);
//				System.out.println("content:");
				/*
				BufferedInputStream is=null;
				int i;
				try {
					is=new BufferedInputStream(((BinaryBody) body).getInputStream());
					while ((i=is.read())!=-1){
						System.out.print((char)i);
					}
//					System.out.println();
				} catch (IOException e) {
					e.printStackTrace();
				}finally{
					try {
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				*/
			}else {
				type = "Text body";
				type += " (" + entity.getMimeType() + ")";
//				System.out.println(type);
//				System.out.println("content:");
				BufferedReader reader=null;
				content=new StringBuffer();
				try {
					reader = new BufferedReader(((TextBody)body).getReader());
					while ((temp=reader.readLine())!=null){
					    content.append(temp);
					}
					//保存一段正文
					textBodyList.add(content.toString());
				} catch (IOException e) {
					e.printStackTrace();
				}finally{
					try {
						reader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
//			System.out.println("````````````````");
//			System.out.println();
		}
	}
}
