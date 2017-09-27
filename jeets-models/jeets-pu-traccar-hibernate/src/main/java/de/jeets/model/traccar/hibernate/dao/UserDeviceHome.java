package de.jeets.model.traccar.hibernate.dao;
// Generated 26.11.2016 13:04:29 by Hibernate Tools 5.2.0.Beta1

import java.util.List;
import javax.naming.InitialContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.hibernate.SessionFactory;
import org.jeets.model.traccar.hibernate.UserDevice;
import org.jeets.model.traccar.hibernate.UserDeviceId;

import static org.hibernate.criterion.Example.create;

/**
 * Home object for domain model class UserDevice.
 * @see org.jeets.model.traccar.hibernate.UserDevice
 * @author Hibernate Tools
 */
public class UserDeviceHome {

	private static final Log log = LogFactory.getLog(UserDeviceHome.class);

	private final SessionFactory sessionFactory = getSessionFactory();

	protected SessionFactory getSessionFactory() {
		try {
			return (SessionFactory) new InitialContext().lookup("SessionFactory");
		} catch (Exception e) {
			log.error("Could not locate SessionFactory in JNDI", e);
			throw new IllegalStateException("Could not locate SessionFactory in JNDI");
		}
	}

	public void persist(UserDevice transientInstance) {
		log.debug("persisting UserDevice instance");
		try {
			sessionFactory.getCurrentSession().persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(UserDevice instance) {
		log.debug("attaching dirty UserDevice instance");
		try {
			sessionFactory.getCurrentSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(UserDevice instance) {
		log.debug("attaching clean UserDevice instance");
		try {
			sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void delete(UserDevice persistentInstance) {
		log.debug("deleting UserDevice instance");
		try {
			sessionFactory.getCurrentSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public UserDevice merge(UserDevice detachedInstance) {
		log.debug("merging UserDevice instance");
		try {
			UserDevice result = (UserDevice) sessionFactory.getCurrentSession().merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public UserDevice findById(org.jeets.model.traccar.hibernate.UserDeviceId id) {
		log.debug("getting UserDevice instance with id: " + id);
		try {
			UserDevice instance = (UserDevice) sessionFactory.getCurrentSession()
					.get("de.jeets.model.traccar.hibernate.UserDevice", id);
			if (instance == null) {
				log.debug("get successful, no instance found");
			} else {
				log.debug("get successful, instance found");
			}
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List<UserDevice> findByExample(UserDevice instance) {
		log.debug("finding UserDevice instance by example");
		try {
			List<UserDevice> results = (List<UserDevice>) sessionFactory.getCurrentSession()
					.createCriteria("de.jeets.model.traccar.hibernate.UserDevice").add(create(instance)).list();
			log.debug("find by example successful, result size: " + results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}
}
