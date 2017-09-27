package de.jeets.model.traccar.hibernate.dao;
// Generated 26.11.2016 13:04:29 by Hibernate Tools 5.2.0.Beta1

import java.util.List;
import javax.naming.InitialContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.hibernate.SessionFactory;
import org.jeets.model.traccar.hibernate.Notifications;

import static org.hibernate.criterion.Example.create;

/**
 * Home object for domain model class Notifications.
 * @see org.jeets.model.traccar.hibernate.Notifications
 * @author Hibernate Tools
 */
public class NotificationsHome {

	private static final Log log = LogFactory.getLog(NotificationsHome.class);

	private final SessionFactory sessionFactory = getSessionFactory();

	protected SessionFactory getSessionFactory() {
		try {
			return (SessionFactory) new InitialContext().lookup("SessionFactory");
		} catch (Exception e) {
			log.error("Could not locate SessionFactory in JNDI", e);
			throw new IllegalStateException("Could not locate SessionFactory in JNDI");
		}
	}

	public void persist(Notifications transientInstance) {
		log.debug("persisting Notifications instance");
		try {
			sessionFactory.getCurrentSession().persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(Notifications instance) {
		log.debug("attaching dirty Notifications instance");
		try {
			sessionFactory.getCurrentSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(Notifications instance) {
		log.debug("attaching clean Notifications instance");
		try {
			sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void delete(Notifications persistentInstance) {
		log.debug("deleting Notifications instance");
		try {
			sessionFactory.getCurrentSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public Notifications merge(Notifications detachedInstance) {
		log.debug("merging Notifications instance");
		try {
			Notifications result = (Notifications) sessionFactory.getCurrentSession().merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public Notifications findById(int id) {
		log.debug("getting Notifications instance with id: " + id);
		try {
			Notifications instance = (Notifications) sessionFactory.getCurrentSession()
					.get("de.jeets.model.traccar.hibernate.Notifications", id);
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

	public List<Notifications> findByExample(Notifications instance) {
		log.debug("finding Notifications instance by example");
		try {
			List<Notifications> results = (List<Notifications>) sessionFactory.getCurrentSession()
					.createCriteria("de.jeets.model.traccar.hibernate.Notifications").add(create(instance)).list();
			log.debug("find by example successful, result size: " + results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}
}
