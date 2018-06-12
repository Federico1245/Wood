/**
 * 
 */
package utils;

import net.sf.hibernate.FlushMode;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.Transaction;
import net.sf.hibernate.cfg.Configuration;
import domain.PersistenceObject;

/**
 * @author Fede
 *
 */
public class PersistenceManager {

	private static PersistenceManager singleton;
	
	private static SessionFactory sessionFactory;
	
	public static PersistenceManager getSingleton() {
		if (singleton == null) {
			singleton = new PersistenceManager();
		}
		
		return singleton;
	}
	
	/**
	 * 
	 * @return
	 * @throws HibernateException
	 */
	private Configuration getHibernateConfiguration() throws HibernateException {
		Configuration configuration = new Configuration();
		configuration = configuration.configure();
		
		return configuration;
	}
	
	/**
	 * 
	 * @return
	 * @throws NCRHomeException
	 */
	protected Session getSession() {
		Session aSession = null;
		try {
			if ( getSessionFactory() != null ) {
				aSession = getSessionFactory().openSession();
			}
		} catch (HibernateException e) {
			e.printStackTrace();
		}
		return aSession;
	}
	
	/**
	 * 
	 */
	protected SessionFactory getSessionFactory() {
		if ( sessionFactory == null ) {
			try {
				sessionFactory = getHibernateConfiguration().buildSessionFactory();
			} catch (HibernateException e) {
				e.printStackTrace();
			}
		}
		return sessionFactory;
	}
	
	/**
	 * Closes the session. If an error ocurres while trying to save the session,
	 * the error is logged but an exception is NOT thrown
	 * 
	 * @param session Session to be closed
	 */
	protected void closeSession(Session session) {
		if (session != null) {
			try {
				session.close();
			} catch (HibernateException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 
	 * @param object
	 */
	public void save(PersistenceObject object) {
		Session session = getSession();
		session.setFlushMode(FlushMode.COMMIT);
		Transaction transaction = null ;
		boolean saved = (object.getId()==null)?(true):(false);
		try {
			transaction = session.beginTransaction();
			session.saveOrUpdate(object);
			if (saved) {
				session.update(object);
			}			
			transaction.commit();
		} catch (HibernateException e) {
			e.printStackTrace();
		} finally {
			closeSession(session);
		}		
	}
	
}
