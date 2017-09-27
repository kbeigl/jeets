package de.jeets.model.traccar.jpa.ejb;
// Generated 26.11.2016 12:59:23 by Hibernate Tools 5.2.0.Beta1

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jeets.model.traccar.jpa.Databasechangelog;
import de.jeets.model.traccar.jpa.DatabasechangelogId;

/**
 * Home object for domain model class Databasechangelog.
 * @see de.jeets.model.traccar.jpa.Databasechangelog
 * @author Hibernate Tools
 */
@Stateless
public class DatabasechangelogHome {

	private static final Log log = LogFactory.getLog(DatabasechangelogHome.class);

	@PersistenceContext
	private EntityManager entityManager;

	public void persist(Databasechangelog transientInstance) {
		log.debug("persisting Databasechangelog instance");
		try {
			entityManager.persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void remove(Databasechangelog persistentInstance) {
		log.debug("removing Databasechangelog instance");
		try {
			entityManager.remove(persistentInstance);
			log.debug("remove successful");
		} catch (RuntimeException re) {
			log.error("remove failed", re);
			throw re;
		}
	}

	public Databasechangelog merge(Databasechangelog detachedInstance) {
		log.debug("merging Databasechangelog instance");
		try {
			Databasechangelog result = entityManager.merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public Databasechangelog findById(DatabasechangelogId id) {
		log.debug("getting Databasechangelog instance with id: " + id);
		try {
			Databasechangelog instance = entityManager.find(Databasechangelog.class, id);
			log.debug("get successful");
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}
}
