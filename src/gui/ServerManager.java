package gui;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Properties;

import server.bin.EmailServerStart;

public class ServerManager {
	private static ServerManager manager;
	private Properties properties = new Properties();
	
	private ServerManager(){
		try {
			properties.load(new FileInputStream("src/server/bin/emailConf.properties"));
		} catch (FileNotFoundException e) {
			EmailServerStart.serverLog.error("server:读取邮件服务器配置文件失败");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	synchronized public static ServerManager getServerManager(){
		if(manager==null){
			manager=new ServerManager();
		}
		return manager;
	}
	
	/**
	 * 读取相应的值
	 * @param key
	 * @return
	 */
	public String getPropertyValue(String key){
		Enumeration en=properties.propertyNames();
		while(en.hasMoreElements()){
			if(key.equals((String) en.nextElement())){
				String value=properties.getProperty(key);
				return value;
			}
		}
		return "null";
	}
	
	/**
	 * 保存属性更改
	 * @param key
	 */
	public void savePropertyByKey(String key,String newValue){
		String oldValue=properties.getProperty(key);
		if(!oldValue.equals(newValue)){
			properties.setProperty(key, newValue);
		}
	}
	
	/**
	 * 保存属性到文件
	 */
	public void saveProperty(){
		try {
			OutputStream fos = new FileOutputStream("src/server/bin/emailConf.properties");
			properties.store(fos, "");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 启动服务器
	 * @return
	 */
	public boolean startServer(){
		return EmailServerStart.startEmailServer();
	}
	
	/**
	 * 停止服务器
	 * @return
	 */
	public boolean stopServer(){
		return EmailServerStart.stopEmailServer();
	}
}
