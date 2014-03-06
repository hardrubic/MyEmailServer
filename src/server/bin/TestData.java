package server.bin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import server.model.Email;
import server.model.User;

public class TestData {
	public static void initTest() {
		UserListManager userList = null;// EmailServerStart.userList;
		userList.addUser("heng", null);

		userList.getUser("heng").getEmailList()
				.add(getEmail(readFile("content1.txt")));
		userList.getUser("heng").getEmailList()
				.add(getEmail(readFile("content2.txt")));
		userList.getUser("heng").getEmailList()
				.add(getEmail(readFile("content3.txt")));
		userList.getUser("heng").getEmailList()
				.add(getEmail(readFile("content4.txt")));
		userList.getUser("heng").getEmailList()
				.add(getEmail(readFile("content5.txt")));
	}

	public static Email getEmail(String content) {
		Email e = new Email();
		e.setContent(content);
		return e;
	}

	public static String readFile(String fileName) {
		File file = new File("src/temp/" + fileName);
		StringBuffer sb = new StringBuffer();
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String s = null;
			while ((s = br.readLine()) != null) {
				sb.append(s);
				sb.append("\n");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
}
