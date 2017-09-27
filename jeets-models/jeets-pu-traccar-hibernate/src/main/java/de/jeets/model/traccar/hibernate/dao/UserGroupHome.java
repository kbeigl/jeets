package de.jeets.model.traccar.hibernate.dao;
// Generated 26.11.2016 13:04:29 by Hibernate Tools 5.2.0.Beta1

import java.util.List;
import javax.naming.InitialContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.hibernate.SessionFactory;
import org.jeets.model.traccar.hibernate.UserGroup;
import org.jeets.model.traccar.hibernate.UserGroupId;

import static org.hibernate.criterion.Example.create;

/**
 * Home object for domain model class UserGroup.
 * @see org.jeets.model.traccar.hibernate.UserGroup
 * @author Hibernate Tools
 */
public class UserGroupHome {

	private static final Log log = LogFactory.getLog(UserGroupHome.class);

	private final SessionFactory sessionFactory = getSessionFactory();

	protected SessionFactory getSessionFactory() {
		try {
			return (SessionFactory) new InitialContext().lookup("SessionFactory");
		} catch (Exception e) {
			log.error("Could not locate SessionFactory in JNDI", e);
			throw new IllegalStateException("Could not locate SessionFactory in JNDI");
		}
	}

	public void persist(UserGroup transientInstance) {
		log.debug("persisting UserGroup instance");
		try {
			sessionFactory.getCurrentSession().persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(UserGroup instance) {
		log.debug("attaching dirty UserGroup instance");
		try {
			sessionFactory.getCurrentSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(UserGroup instance) {
		log.debug("attaching clean UserGroup instance");
		try {
			sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void delete(UserGroup persistentInstance) {
		log.debug("deleting UserGroup instance");
		try {
			sessionFactory.getCurrentSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public UserGroup merge(UserGroup detachedInstance) {
		log.debug("merging UserGroup instance");
		try {
			UserGroup result = (UserGroup) sessionFactory.getCurrentSession().merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public UserGroup findById(org.jeets.model.traccar.hibernate.UserGroupId id) {
		log.debug("getting UserGroup instance with id: " + id);
		try {
			UserGroup instance = (UserGroup) sessionFactory.getCurrentSession()
					.get("de.jeets.model.traccar.hibernate.UserGroup", id);
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

	public List<UserGroup> findByExample(UserGroup instance) {
		log.debug("finding UserGroup instance by example");
		try {
			List<UserGroup> results = (List<UserGroup>) sessionFactory.getCurrentSession()
					.createCriteria("de.jeets.model.traccar.hibernate.UserGroup").add(create(instance)).list();
			log.debug("find by example successful, result size: " + results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}
}
