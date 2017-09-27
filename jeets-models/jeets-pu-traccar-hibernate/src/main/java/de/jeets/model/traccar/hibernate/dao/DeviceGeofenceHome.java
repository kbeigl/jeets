package de.jeets.model.traccar.hibernate.dao;
// Generated 26.11.2016 13:04:29 by Hibernate Tools 5.2.0.Beta1

import java.util.List;
import javax.naming.InitialContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.hibernate.SessionFactory;
import org.jeets.model.traccar.hibernate.DeviceGeofence;
import org.jeets.model.traccar.hibernate.DeviceGeofenceId;

import static org.hibernate.criterion.Example.create;

/**
 * Home object for domain model class DeviceGeofence.
 * @see org.jeets.model.traccar.hibernate.DeviceGeofence
 * @author Hibernate Tools
 */
public class DeviceGeofenceHome {

	private static final Log log = LogFactory.getLog(DeviceGeofenceHome.class);

	private final SessionFactory sessionFactory = getSessionFactory();

	protected SessionFactory getSessionFactory() {
		try {
			return (SessionFactory) new InitialContext().lookup("SessionFactory");
		} catch (Exception e) {
			log.error("Could not locate SessionFactory in JNDI", e);
			throw new IllegalStateException("Could not locate SessionFactory in JNDI");
		}
	}

	public void persist(DeviceGeofence transientInstance) {
		log.debug("persisting DeviceGeofence instance");
		try {
			sessionFactory.getCurrentSession().persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(DeviceGeofence instance) {
		log.debug("attaching dirty DeviceGeofence instance");
		try {
			sessionFactory.getCurrentSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(DeviceGeofence instance) {
		log.debug("attaching clean DeviceGeofence instance");
		try {
			sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void delete(DeviceGeofence persistentInstance) {
		log.debug("deleting DeviceGeofence instance");
		try {
			sessionFactory.getCurrentSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public DeviceGeofence merge(DeviceGeofence detachedInstance) {
		log.debug("merging DeviceGeofence instance");
		try {
			DeviceGeofence result = (DeviceGeofence) sessionFactory.getCurrentSession().merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public DeviceGeofence findById(org.jeets.model.traccar.hibernate.DeviceGeofenceId id) {
		log.debug("getting DeviceGeofence instance with id: " + id);
		try {
			DeviceGeofence instance = (DeviceGeofence) sessionFactory.getCurrentSession()
					.get("de.jeets.model.traccar.hibernate.DeviceGeofence", id);
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

	public List<DeviceGeofence> findByExample(DeviceGeofence instance) {
		log.debug("finding DeviceGeofence instance by example");
		try {
			List<DeviceGeofence> results = (List<DeviceGeofence>) sessionFactory.getCurrentSession()
					.createCriteria("de.jeets.model.traccar.hibernate.DeviceGeofence").add(create(instance)).list();
			log.debug("find by example successful, result size: " + results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}
}
