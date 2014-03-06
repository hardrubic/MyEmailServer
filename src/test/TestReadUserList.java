package test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import server.bin.UserListManager;
import server.model.User;

public class TestReadUserList {
	static List<User> userList;
	
	public static void main(String[] args) {
		ObjectInputStream ois=null;
		ObjectOutputStream oos=null;
		try {
			File userListFile=new File("src/server/bin/userListFile");
			if(userListFile.exists()){
				ois=new ObjectInputStream(new FileInputStream(userListFile));
				userList= (List<User>) ois.readObject();
			}else {
				userListFile.createNewFile();
				oos=new ObjectOutputStream(new FileOutputStream(userListFile));
				userList = new ArrayList<User>();
				oos.writeObject(userList);
			}
			System.out.println("用户数量:"+userList.size());
			for (int i = 0; i < userList.size(); i++) {
				User user=userList.get(i);
				System.out.println(user.getUserName()+"  "+user.getPassword()+" 邮件数量"+user.getEmailNumber());
			}
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
}
