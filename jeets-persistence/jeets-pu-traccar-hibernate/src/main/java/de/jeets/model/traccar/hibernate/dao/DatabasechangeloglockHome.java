package de.jeets.model.traccar.hibernate.dao;
// Generated 26.11.2016 13:04:29 by Hibernate Tools 5.2.0.Beta1

import java.util.List;
import javax.naming.InitialContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.hibernate.SessionFactory;
import org.jeets.model.traccar.hibernate.Databasechangeloglock;

import static org.hibernate.criterion.Example.create;

/**
 * Home object for domain model class Databasechangeloglock.
 * @see org.jeets.model.traccar.hibernate.Databasechangeloglock
 * @author Hibernate Tools
 */
public class DatabasechangeloglockHome {

	private static final Log log = LogFactory.getLog(DatabasechangeloglockHome.class);

	private final SessionFactory sessionFactory = getSessionFactory();

	protected SessionFactory getSessionFactory() {
		try {
			return (SessionFactory) new InitialContext().lookup("SessionFactory");
		} catch (Exception e) {
			log.error("Could not locate SessionFactory in JNDI", e);
			throw new IllegalStateException("Could not locate SessionFactory in JNDI");
		}
	}

	public void persist(Databasechangeloglock transientInstance) {
		log.debug("persisting Databasechangeloglock instance");
		try {
			sessionFactory.getCurrentSession().persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(Databasechangeloglock instance) {
		log.debug("attaching dirty Databasechangeloglock instance");
		try {
			sessionFactory.getCurrentSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(Databasechangeloglock instance) {
		log.debug("attaching clean Databasechangeloglock instance");
		try {
			sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void delete(Databasechangeloglock persistentInstance) {
		log.debug("deleting Databasechangeloglock instance");
		try {
			sessionFactory.getCurrentSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public Databasechangeloglock merge(Databasechangeloglock detachedInstance) {
		log.debug("merging Databasechangeloglock instance");
		try {
			Databasechangeloglock result = (Databasechangeloglock) sessionFactory.getCurrentSession()
					.merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public Databasechangeloglock findById(int id) {
		log.debug("getting Databasechangeloglock instance with id: " + id);
		try {
			Databasechangeloglock instance = (Databasechangeloglock) sessionFactory.getCurrentSession()
					.get("de.jeets.model.traccar.hibernate.Databasechangeloglock", id);
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

	public List<Databasechangeloglock> findByExample(Databasechangeloglock instance) {
		log.debug("finding Databasechangeloglock instance by example");
		try {
			List<Databasechangeloglock> results = (List<Databasechangeloglock>) sessionFactory.getCurrentSession()
					.createCriteria("de.jeets.model.traccar.hibernate.Databasechangeloglock").add(create(instance))
					.list();
			log.debug("find by example successful, result size: " + results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}
}
