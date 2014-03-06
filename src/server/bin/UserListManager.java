package server.bin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import server.model.User;
import server.util.CheckUtil;
import server.util.StringUtil;
import server.util.EnumUtil.LockType;
import server.util.MD5Util;

public class UserListManager implements Serializable {
	private static final long serialVersionUID = 1L;
	private static UserListManager userListManager;
	private static List<User> userList;

	private UserListManager() {
	}

	public static UserListManager getInstance() {
		if (userListManager == null) {
			userListManager = new UserListManager();
		}
		return userListManager;
	}

	/**
	 * 初始化用户列表
	 */
	public void initUserList() {
		if (userList == null) {
			loadUserList();
		}
	}

	/**
	 * 用户是否已存在
	 * 
	 * @param user
	 * @return
	 */
	public boolean checkUserExist(String user) {
		if (CheckUtil.isEmailAddress(user)) {
			// 如果输入的是邮箱地址
			user = StringUtil.getEmailName(user);
		}
		for (int i = 0; i < userList.size(); i++) {
			if (userList.get(i).getUserName().equals(user)) {
				// 用户已存在
				return true;
			}
		}
		return false;
	}

	/**
	 * 检查用户登陆
	 * 
	 * @param userName
	 * @param password
	 * @return
	 */
	public boolean checkUserLogin(String userName, String password) {
		if (checkUserExist(userName)) {
			// 用户存在
			User user = getUser(userName);
			if (user.getPassword().equals(MD5Util.getMD5(password))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 增加一个用户
	 * 
	 * @param user
	 * @param password
	 * @return
	 */
	public boolean addUser(String user, String password) {
		if (checkUserExist(user)) {
			return false;
		}
		// 加密密码
		User u = new User(user, MD5Util.getMD5(password));
		synchronized (userList) {
			userList.add(u);
		}
		// 持久化
		writeUserList();
		return true;
	}

	/**
	 * 删除一个用户
	 * 
	 * @param userName
	 * @return
	 */
	public boolean delUser(String userName) {
		for (int i = 0; i < userList.size(); i++) {
			if (userName.equals(userList.get(i).getUserName())) {
				userList.remove(i);
				writeUserList();
				return true;
			}
		}
		return false;
	}

	/**
	 * 获取一个用户实例
	 * 
	 * @param userName
	 * @return
	 */
	public User getUser(String userName) {
		for (int i = 0; i < userList.size(); i++) {
			if (userName.equals(userList.get(i).getUserName())) {
				return userList.get(i);
			}
		}
		return null;
	}

	/**
	 * 检查用户是否被锁定
	 * 
	 * @param user
	 * @param lockType
	 * @return
	 */
	public boolean isUserLocked(String userName, LockType lockType) {
		for (int i = 0; i < userList.size(); i++) {
			User user = userList.get(i);
			if (user.getUserName().equals(userName)) {
				if (lockType.equals(lockType.SMTP)) {
					if (user.isLockedBySMTP()) {
						return true;
					}
				} else if (lockType.equals(lockType.POP3)) {
					if (user.isLockedByPOP3()) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * 锁定用户
	 * 
	 * @param userName
	 * @param lockType
	 * @return
	 */
	public boolean lockUser(String userName, LockType lockType) {
		for (int i = 0; i < userList.size(); i++) {
			if (userList.get(i).getUserName().equals(userName)) {
				synchronized (userList) {
					if (lockType.equals(lockType.SMTP)) {
						userList.get(i).setLockedBySMTP(true);
					} else if (lockType.equals(lockType.POP3)) {
						userList.get(i).setLockedByPOP3(true);
					}
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * 解锁用户
	 * 
	 * @param user
	 * @param lockType
	 * @return
	 */
	public boolean unlockUser(String user, LockType lockType) {
		for (int i = 0; i < userList.size(); i++) {
			if (userList.get(i).getUserName().equals(user)) {
				synchronized (userList) {
					if (lockType.equals(lockType.SMTP)) {
						userList.get(i).setLockedBySMTP(false);
					} else if (lockType.equals(lockType.POP3)) {
						userList.get(i).setLockedByPOP3(false);
					}
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * 加载用户列表
	 */
	private void loadUserList() {
		ObjectInputStream ois = null;
		ObjectOutputStream oos = null;
		try {
			File userListFile = new File("src/server/bin/userListFile");
			if (userListFile.exists()) {
				ois = new ObjectInputStream(new FileInputStream(userListFile));
				userList = (List<User>) ois.readObject();
			} else {
				userListFile.createNewFile();
				oos = new ObjectOutputStream(new FileOutputStream(userListFile));
				userList = new ArrayList<User>();
				oos.writeObject(userList);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (ois != null) {
				try {
					ois.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 持久化用户列表
	 */
	public void writeUserList() {
		try {
			File userListFile = new File("src/server/bin/userListFile");
			FileOutputStream fos = new FileOutputStream(userListFile);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(userList);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取用户数量
	 * 
	 * @return
	 */
	public int getUserNumber() {
		return userList.size();
	}

	/**
	 * 最好不开放
	 * 
	 * @return
	 */
	public List<User> getUserList() {
		return userList;
	}
}
