package de.jeets.model.traccar.jpa.ejb;
// Generated 26.11.2016 12:59:23 by Hibernate Tools 5.2.0.Beta1

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jeets.model.traccar.jpa.Databasechangeloglock;

/**
 * Home object for domain model class Databasechangeloglock.
 * @see de.jeets.model.traccar.jpa.Databasechangeloglock
 * @author Hibernate Tools
 */
@Stateless
public class DatabasechangeloglockHome {

	private static final Log log = LogFactory.getLog(DatabasechangeloglockHome.class);

	@PersistenceContext
	private EntityManager entityManager;

	public void persist(Databasechangeloglock transientInstance) {
		log.debug("persisting Databasechangeloglock instance");
		try {
			entityManager.persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void remove(Databasechangeloglock persistentInstance) {
		log.debug("removing Databasechangeloglock instance");
		try {
			entityManager.remove(persistentInstance);
			log.debug("remove successful");
		} catch (RuntimeException re) {
			log.error("remove failed", re);
			throw re;
		}
	}

	public Databasechangeloglock merge(Databasechangeloglock detachedInstance) {
		log.debug("merging Databasechangeloglock instance");
		try {
			Databasechangeloglock result = entityManager.merge(detachedInstance);
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
			Databasechangeloglock instance = entityManager.find(Databasechangeloglock.class, id);
			log.debug("get successful");
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}
}
