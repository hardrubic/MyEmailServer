package server.db;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

public class DBConnectionHibernate {
	public static final ThreadLocal<Session> sessionThreadLoacl = new ThreadLocal();
	private static SessionFactory sessionFactory;

	private DBConnectionHibernate() {
	}

	static {
		Configuration cfg = new Configuration();
		cfg.configure();
		ServiceRegistry sr = new ServiceRegistryBuilder().applySettings(cfg.getProperties()).buildServiceRegistry();
		sessionFactory = cfg.buildSessionFactory(sr);
	}

	public static void init() {
		Configuration cfg = new Configuration();
		cfg.configure();
		ServiceRegistry sr = new ServiceRegistryBuilder().applySettings(cfg.getProperties()).buildServiceRegistry();
		sessionFactory = cfg.buildSessionFactory(sr);
	}

	public static SessionFactory getSessionFactoryInstance() {
		if (sessionFactory == null) {
			init();
		}
		return sessionFactory;
	}

	/**
	 * 获取当前的Session
	 * 
	 * @return
	 * @throws HibernateException
	 */
	public static Session currentSession() {
		Session session = (Session) sessionThreadLoacl.get();
		System.out.println("!!!!!!!!!!!!session:"+session);
		if (session == null) {
			session = sessionFactory.openSession();
			sessionThreadLoacl.set(session);
		}
		return session;
	}

	/**
	 * 释放Session
	 * 
	 * @throws HibernateException
	 */
	public static void closeSession() {
		Session session = (Session) sessionThreadLoacl.get();
		if (session != null) {
			session.close();
		}
		sessionThreadLoacl.set(null);

	}

	public static void main(String[] args) {
		SessionFactory sf = DBConnectionHibernate.getSessionFactoryInstance();
		Session session = sf.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();

			tx.commit();
		} catch (Exception e) {
			try {
				if (tx != null)
					tx.rollback();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} finally {
			session.close();
		}
	}
}
