package de.jeets.model.traccar.jpa.ejb;
// Generated 26.11.2016 12:59:23 by Hibernate Tools 5.2.0.Beta1

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jeets.model.traccar.jpa.Positions;

/**
 * Home object for domain model class Positions.
 * @see de.jeets.model.traccar.jpa.Positions
 * @author Hibernate Tools
 */
@Stateless
public class PositionsHome {

	private static final Log log = LogFactory.getLog(PositionsHome.class);

	@PersistenceContext
	private EntityManager entityManager;

	public void persist(Positions transientInstance) {
		log.debug("persisting Positions instance");
		try {
			entityManager.persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void remove(Positions persistentInstance) {
		log.debug("removing Positions instance");
		try {
			entityManager.remove(persistentInstance);
			log.debug("remove successful");
		} catch (RuntimeException re) {
			log.error("remove failed", re);
			throw re;
		}
	}

	public Positions merge(Positions detachedInstance) {
		log.debug("merging Positions instance");
		try {
			Positions result = entityManager.merge(detachedInstance);
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
			Positions instance = entityManager.find(Positions.class, id);
			log.debug("get successful");
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}
}
