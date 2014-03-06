package server.smtp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.LookAndFeel;

import org.apache.log4j.Logger;

import server.bin.EmailServerStart;

import server.model.Email;

public class EmailPersistence {
	private static EmailPersistence ep;
	private String PERSISTENCE_PATH="src/server/email/";	//邮件存放位置
	/**
	 * 日记记录
	 */
	private Logger logger;
	
	private EmailPersistence(){
		logger=EmailServerStart.persistenceLog;
	}
	
	public static EmailPersistence getInstance(){
		if(ep==null){
			ep=new EmailPersistence();
		}
		return ep;
	}
	
	/**
	 * 保存email
	 * @param email
	 */
	public void saveEmail(Email email){
		String uidl=email.getUidl();
		ObjectOutputStream oos=null;
		File file=new File(PERSISTENCE_PATH+uidl+".txt");
		logger.debug("persistence:更新保存"+file.getName());
		try {
			oos = new ObjectOutputStream(new FileOutputStream(file));
			oos.writeObject(email);
		}catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(oos!=null){
				try {
					oos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 读取email
	 * @param uidl
	 * @return
	 */
	public List<Email> loadEmail(){
		List<Email> emailList=new ArrayList<Email>();
		File filePath=new File(PERSISTENCE_PATH);
		if(filePath.isDirectory()){
			String[] fileList=filePath.list();
			if(fileList.length==0){
				//没有文件
				return null;
			}
			for (int i = 0; i < fileList.length; i++) {
				File file=new File(PERSISTENCE_PATH+fileList[i]);
				logger.debug("persistence:加载文件"+file.getName());
				ObjectInputStream ois=null;
				try {
					ois=new ObjectInputStream(new FileInputStream(file));
					emailList.add((Email)ois.readObject());
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}finally{
					if(ois!=null){
						try {
							ois.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				
			}
		}else {
			logger.error("EmailPersistence.loadEmail error");
		}
		return emailList;
	}
	
	/**
	 * 删除email
	 * @param uidl
	 */
	public void deleteEmail(String uidl){
		String path=PERSISTENCE_PATH+uidl+".txt";
		ObjectInputStream ois=null;
		File file=new File(path);
		logger.debug("persistence:删除文件"+path);
		if(file.exists()){
			file.delete();
		}
	}
}
