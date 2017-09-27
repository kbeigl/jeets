package de.jeets.model.traccar.hibernate.dao;
// Generated 26.11.2016 13:04:29 by Hibernate Tools 5.2.0.Beta1

import java.util.List;
import javax.naming.InitialContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.hibernate.SessionFactory;
import org.jeets.model.traccar.hibernate.Positions;

import static org.hibernate.criterion.Example.create;

/**
 * Home object for domain model class Positions.
 * @see org.jeets.model.traccar.hibernate.Positions
 * @author Hibernate Tools
 */
public class PositionsHome {

	private static final Log log = LogFactory.getLog(PositionsHome.class);

	private final SessionFactory sessionFactory = getSessionFactory();

	protected SessionFactory getSessionFactory() {
		try {
			return (SessionFactory) new InitialContext().lookup("SessionFactory");
		} catch (Exception e) {
			log.error("Could not locate SessionFactory in JNDI", e);
			throw new IllegalStateException("Could not locate SessionFactory in JNDI");
		}
	}

	public void persist(Positions transientInstance) {
		log.debug("persisting Positions instance");
		try {
			sessionFactory.getCurrentSession().persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(Positions instance) {
		log.debug("attaching dirty Positions instance");
		try {
			sessionFactory.getCurrentSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(Positions instance) {
		log.debug("attaching clean Positions instance");
		try {
			sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void delete(Positions persistentInstance) {
		log.debug("deleting Positions instance");
		try {
			sessionFactory.getCurrentSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public Positions merge(Positions detachedInstance) {
		log.debug("merging Positions instance");
		try {
			Positions result = (Positions) sessionFactory.getCurrentSession().merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public Positions findById(int id) {
		log.debug("getting Positions instance with id: " + id);
		try {
			Positions instance = (Positions) sessionFactory.getCurrentSession()
					.get("de.jeets.model.traccar.hibernate.Positions", id);
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

	public List<Positions> findByExample(Positions instance) {
		log.debug("finding Positions instance by example");
		try {
			List<Positions> results = (List<Positions>) sessionFactory.getCurrentSession()
					.createCriteria("de.jeets.model.traccar.hibernate.Positions").add(create(instance)).list();
			log.debug("find by example successful, result size: " + results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}
}
