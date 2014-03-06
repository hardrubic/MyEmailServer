package test;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class TestTimer extends TimerTask{
	public static void main(String[] args) {
		Timer timer=new Timer();
		TimerTask task=new TestTimer();
		timer.schedule(task, 500L,3000L);
	}

	@Override
	public void run() {
		Date nowDate=new Date(this.scheduledExecutionTime());
		System.out.println(nowDate);
	}
	
}

