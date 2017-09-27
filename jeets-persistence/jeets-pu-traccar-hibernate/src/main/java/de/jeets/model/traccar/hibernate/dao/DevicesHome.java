package de.jeets.model.traccar.hibernate.dao;
// Generated 26.11.2016 13:04:29 by Hibernate Tools 5.2.0.Beta1

import java.util.List;
import javax.naming.InitialContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.hibernate.SessionFactory;
import org.jeets.model.traccar.hibernate.Devices;

import static org.hibernate.criterion.Example.create;

/**
 * Home object for domain model class Devices.
 * @see org.jeets.model.traccar.hibernate.Devices
 * @author Hibernate Tools
 */
public class DevicesHome {

	private static final Log log = LogFactory.getLog(DevicesHome.class);

	private final SessionFactory sessionFactory = getSessionFactory();

	protected SessionFactory getSessionFactory() {
		try {
			return (SessionFactory) new InitialContext().lookup("SessionFactory");
		} catch (Exception e) {
			log.error("Could not locate SessionFactory in JNDI", e);
			throw new IllegalStateException("Could not locate SessionFactory in JNDI");
		}
	}

	public void persist(Devices transientInstance) {
		log.debug("persisting Devices instance");
		try {
			sessionFactory.getCurrentSession().persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(Devices instance) {
		log.debug("attaching dirty Devices instance");
		try {
			sessionFactory.getCurrentSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(Devices instance) {
		log.debug("attaching clean Devices instance");
		try {
			sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void delete(Devices persistentInstance) {
		log.debug("deleting Devices instance");
		try {
			sessionFactory.getCurrentSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public Devices merge(Devices detachedInstance) {
		log.debug("merging Devices instance");
		try {
			Devices result = (Devices) sessionFactory.getCurrentSession().merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public Devices findById(int id) {
		log.debug("getting Devices instance with id: " + id);
		try {
			Devices instance = (Devices) sessionFactory.getCurrentSession()
					.get("de.jeets.model.traccar.hibernate.Devices", id);
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

	public List<Devices> findByExample(Devices instance) {
		log.debug("finding Devices instance by example");
		try {
			List<Devices> results = (List<Devices>) sessionFactory.getCurrentSession()
					.createCriteria("de.jeets.model.traccar.hibernate.Devices").add(create(instance)).list();
			log.debug("find by example successful, result size: " + results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}
}
