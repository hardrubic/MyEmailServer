package server.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import server.model.Email;

public class DebugUtil {
	public static void printEmail(Email email){
		StringBuffer buffer=new StringBuffer();
		buffer.append("").append(email.getContent()).append('\n');
		
		File file = new File("emailContent.txt");
		try {
			PrintWriter pw = new PrintWriter(file);
			pw.write(new String(buffer));
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
}
