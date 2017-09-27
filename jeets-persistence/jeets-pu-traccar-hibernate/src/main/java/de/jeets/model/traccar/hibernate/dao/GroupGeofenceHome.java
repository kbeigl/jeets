package de.jeets.model.traccar.hibernate.dao;
// Generated 26.11.2016 13:04:29 by Hibernate Tools 5.2.0.Beta1

import java.util.List;
import javax.naming.InitialContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.hibernate.SessionFactory;
import org.jeets.model.traccar.hibernate.GroupGeofence;
import org.jeets.model.traccar.hibernate.GroupGeofenceId;

import static org.hibernate.criterion.Example.create;

/**
 * Home object for domain model class GroupGeofence.
 * @see org.jeets.model.traccar.hibernate.GroupGeofence
 * @author Hibernate Tools
 */
public class GroupGeofenceHome {

	private static final Log log = LogFactory.getLog(GroupGeofenceHome.class);

	private final SessionFactory sessionFactory = getSessionFactory();

	protected SessionFactory getSessionFactory() {
		try {
			return (SessionFactory) new InitialContext().lookup("SessionFactory");
		} catch (Exception e) {
			log.error("Could not locate SessionFactory in JNDI", e);
			throw new IllegalStateException("Could not locate SessionFactory in JNDI");
		}
	}

	public void persist(GroupGeofence transientInstance) {
		log.debug("persisting GroupGeofence instance");
		try {
			sessionFactory.getCurrentSession().persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(GroupGeofence instance) {
		log.debug("attaching dirty GroupGeofence instance");
		try {
			sessionFactory.getCurrentSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(GroupGeofence instance) {
		log.debug("attaching clean GroupGeofence instance");
		try {
			sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void delete(GroupGeofence persistentInstance) {
		log.debug("deleting GroupGeofence instance");
		try {
			sessionFactory.getCurrentSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public GroupGeofence merge(GroupGeofence detachedInstance) {
		log.debug("merging GroupGeofence instance");
		try {
			GroupGeofence result = (GroupGeofence) sessionFactory.getCurrentSession().merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public GroupGeofence findById(org.jeets.model.traccar.hibernate.GroupGeofenceId id) {
		log.debug("getting GroupGeofence instance with id: " + id);
		try {
			GroupGeofence instance = (GroupGeofence) sessionFactory.getCurrentSession()
					.get("de.jeets.model.traccar.hibernate.GroupGeofence", id);
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

	public List<GroupGeofence> findByExample(GroupGeofence instance) {
		log.debug("finding GroupGeofence instance by example");
		try {
			List<GroupGeofence> results = (List<GroupGeofence>) sessionFactory.getCurrentSession()
					.createCriteria("de.jeets.model.traccar.hibernate.GroupGeofence").add(create(instance)).list();
			log.debug("find by example successful, result size: " + results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}
}
