package de.jeets.model.traccar.hibernate.dao;
// Generated 26.11.2016 13:04:29 by Hibernate Tools 5.2.0.Beta1

import java.util.List;
import javax.naming.InitialContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.hibernate.SessionFactory;
import org.jeets.model.traccar.hibernate.UserGeofence;
import org.jeets.model.traccar.hibernate.UserGeofenceId;

import static org.hibernate.criterion.Example.create;

/**
 * Home object for domain model class UserGeofence.
 * @see org.jeets.model.traccar.hibernate.UserGeofence
 * @author Hibernate Tools
 */
public class UserGeofenceHome {

	private static final Log log = LogFactory.getLog(UserGeofenceHome.class);

	private final SessionFactory sessionFactory = getSessionFactory();

	protected SessionFactory getSessionFactory() {
		try {
			return (SessionFactory) new InitialContext().lookup("SessionFactory");
		} catch (Exception e) {
			log.error("Could not locate SessionFactory in JNDI", e);
			throw new IllegalStateException("Could not locate SessionFactory in JNDI");
		}
	}

	public void persist(UserGeofence transientInstance) {
		log.debug("persisting UserGeofence instance");
		try {
			sessionFactory.getCurrentSession().persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(UserGeofence instance) {
		log.debug("attaching dirty UserGeofence instance");
		try {
			sessionFactory.getCurrentSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(UserGeofence instance) {
		log.debug("attaching clean UserGeofence instance");
		try {
			sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void delete(UserGeofence persistentInstance) {
		log.debug("deleting UserGeofence instance");
		try {
			sessionFactory.getCurrentSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public UserGeofence merge(UserGeofence detachedInstance) {
		log.debug("merging UserGeofence instance");
		try {
			UserGeofence result = (UserGeofence) sessionFactory.getCurrentSession().merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public UserGeofence findById(org.jeets.model.traccar.hibernate.UserGeofenceId id) {
		log.debug("getting UserGeofence instance with id: " + id);
		try {
			UserGeofence instance = (UserGeofence) sessionFactory.getCurrentSession()
					.get("de.jeets.model.traccar.hibernate.UserGeofence", id);
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

	public List<UserGeofence> findByExample(UserGeofence instance) {
		log.debug("finding UserGeofence instance by example");
		try {
			List<UserGeofence> results = (List<UserGeofence>) sessionFactory.getCurrentSession()
					.createCriteria("de.jeets.model.traccar.hibernate.UserGeofence").add(create(instance)).list();
			log.debug("find by example successful, result size: " + results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}
}
