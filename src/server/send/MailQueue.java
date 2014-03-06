package server.send;

import java.util.*;

import org.apache.log4j.Logger;

import server.bin.EmailServerStart;

import server.model.Email;

public class MailQueue {
	/**
	 * 邮件队列
	 */
	private static MailQueue mailQueue;
	/**
	 * 邮件发送队列
	 */
	private static Queue<Email> sendQueue = new LinkedList<Email>();
	/**
	 * 邮件等待队列
	 */
	private static Queue<Email> waitQueue = new LinkedList<Email>();
	/**
	 * 记录邮件发送次数
	 */
	private static Map<String, Integer> sendTimeMap = new HashMap<String, Integer>();
	/**
	 * 日记记录
	 */
	private Logger logger;
	
	private MailQueue(){
		logger=EmailServerStart.sendLog;
	}
	
	public static MailQueue getInstance(){
		if(mailQueue==null){
			mailQueue=new MailQueue();
		}
		return mailQueue;
	}
	
	/**
	 * 从邮件发送队列取出一封邮件
	 * @return
	 */
	public Email getEmailFromSendQueue(){
		Email email=null;
		synchronized (sendQueue) {
			logger.debug("send:从sendQueue取出一封邮件");
			email=sendQueue.poll();
		}
		return email;
	}
	
	/**
	 * 从邮件等待队列取出一封邮件
	 * @return
	 */
	public Email getEmailFromWaitQueue(){
		Email email=null;
		synchronized (waitQueue) {
			logger.debug("send:从waitQueue取出一封邮件");
			email=waitQueue.poll();
		}
		return email;
	}
	
	/**
	 * 向邮件发送队列增加一封邮件
	 * @param email
	 * @return
	 */
	public boolean addEmailToSendQueue(Email email){
		boolean flag=false;
		synchronized (sendQueue) {
			logger.debug("send:向sendQueue加入一封邮件");
			flag=sendQueue.offer(email);
		}
		//初始化邮件发送次数
		if(!sendTimeMap.containsKey(email.getUidl())){
			sendTimeMap.put(email.getUidl(), 0);
		}
		return flag;
	}
	
	/**
	 * 向邮件等待队列增加一封邮件
	 * @param email
	 * @return
	 */
	public boolean addEmailToWaitQueue(Email email){
		synchronized (waitQueue) {
			logger.debug("send:向waitQueue加入一封邮件");
			return waitQueue.offer(email);
		}
	}
	
	/**
	 * 判断发送队列是否为空
	 * @return
	 */
	public boolean isSendQueueEmpty(){
		if(sendQueue.size()==0){
			return true;
		}
		return false;
	}
	
	/**
	 * 判断等待队列是否为空
	 * @return
	 */
	public boolean isWaitQueueEmpty(){
		if(waitQueue.size()==0){
			return true;
		}
		return false;
	}
	
	/**
	 * 增加邮件发送次数
	 * @param email
	 */
	public void addSendTime(Email email){
		String uidl=email.getUidl();
		if(sendTimeMap.containsKey(uidl)){
			int time=sendTimeMap.get(uidl);
			sendTimeMap.remove(uidl);
			sendTimeMap.put(uidl, time+1);
			logger.info("send:第"+(time+1)+"次尝试发送邮件"+email.getUidl());
		}else {
			logger.error("send:增加发送次数出错");
		}
	}
	
	/**
	 * 查询是否到达发送次数上限
	 * @param email
	 * @return
	 */
	public boolean reachMaxSendTime(Email email){
		String uidl=email.getUidl();
		if(sendTimeMap.containsKey(uidl)){
			int time=sendTimeMap.get(uidl);
			if(time>=3){
				return true;
			}
		}else {
			logger.error("send:查询发送次数出错");
		}
		return false;
	}
	
	/**
	 * 删除邮件发送记录次数
	 * @param email
	 */
	public void removeSendTime(Email email){
		String uidl=email.getUidl();
		if(sendTimeMap.containsKey(uidl)){
			sendTimeMap.remove(uidl);
		}else {
			logger.error("send:删除发送次数出错");
		}
	}
}
