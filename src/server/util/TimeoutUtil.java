package server.util;

import java.io.BufferedReader;
import java.io.IOException;

public class TimeoutUtil implements Runnable {
	private BufferedReader in;
	private String strLine = null;
	private boolean isReading = true;

	public TimeoutUtil(BufferedReader in) {
		this.in = in;
	}

	public static String limitTimeReadLine(BufferedReader in, long timeout) {
		long currentTime = System.currentTimeMillis();
		long endTime = currentTime + timeout;

		TimeoutUtil tu=new TimeoutUtil(in);
		Thread timeThread=new Thread(tu);
		timeThread.start();
		
		while(currentTime<=endTime){
			Thread.yield();
			currentTime=System.currentTimeMillis();
			if(tu.isReading==false){
				//已经读取一行数据
				break;
			}
		}
		
		if(tu.isReading==false){
			//已经读取一行数据
			timeThread.interrupt();
		}else{
			//超时没有读取信息
			timeThread.interrupt();
		}
		return tu.strLine;
	}

	@Override
	public void run() {
		try {
			strLine = in.readLine();
		} catch (IOException e) {
			//e.printStackTrace();
		}
		// 已经读入一行数据
		isReading = false;
	}
}
