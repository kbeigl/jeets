package de.jeets.model.traccar.jpa.ejb;
// Generated 26.11.2016 12:59:23 by Hibernate Tools 5.2.0.Beta1

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jeets.model.traccar.jpa.Geofences;

/**
 * Home object for domain model class Geofences.
 * @see de.jeets.model.traccar.jpa.Geofences
 * @author Hibernate Tools
 */
@Stateless
public class GeofencesHome {

	private static final Log log = LogFactory.getLog(GeofencesHome.class);

	@PersistenceContext
	private EntityManager entityManager;

	public void persist(Geofences transientInstance) {
		log.debug("persisting Geofences instance");
		try {
			entityManager.persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void remove(Geofences persistentInstance) {
		log.debug("removing Geofences instance");
		try {
			entityManager.remove(persistentInstance);
			log.debug("remove successful");
		} catch (RuntimeException re) {
			log.error("remove failed", re);
			throw re;
		}
	}

	public Geofences merge(Geofences detachedInstance) {
		log.debug("merging Geofences instance");
		try {
			Geofences result = entityManager.merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public Geofences findById(int id) {
		log.debug("getting Geofences instance with id: " + id);
		try {
			Geofences instance = entityManager.find(Geofences.class, id);
			log.debug("get successful");
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}
}
