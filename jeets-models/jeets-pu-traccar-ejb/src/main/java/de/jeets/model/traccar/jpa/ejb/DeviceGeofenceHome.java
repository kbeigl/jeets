package de.jeets.model.traccar.jpa.ejb;
// Generated 26.11.2016 12:59:23 by Hibernate Tools 5.2.0.Beta1

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jeets.model.traccar.jpa.DeviceGeofence;
import de.jeets.model.traccar.jpa.DeviceGeofenceId;

/**
 * Home object for domain model class DeviceGeofence.
 * @see de.jeets.model.traccar.jpa.DeviceGeofence
 * @author Hibernate Tools
 */
@Stateless
public class DeviceGeofenceHome {

	private static final Log log = LogFactory.getLog(DeviceGeofenceHome.class);

	@PersistenceContext
	private EntityManager entityManager;

	public void persist(DeviceGeofence transientInstance) {
		log.debug("persisting DeviceGeofence instance");
		try {
			entityManager.persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void remove(DeviceGeofence persistentInstance) {
		log.debug("removing DeviceGeofence instance");
		try {
			entityManager.remove(persistentInstance);
			log.debug("remove successful");
		} catch (RuntimeException re) {
			log.error("remove failed", re);
			throw re;
		}
	}

	public DeviceGeofence merge(DeviceGeofence detachedInstance) {
		log.debug("merging DeviceGeofence instance");
		try {
			DeviceGeofence result = entityManager.merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public DeviceGeofence findById(DeviceGeofenceId id) {
		log.debug("getting DeviceGeofence instance with id: " + id);
		try {
			DeviceGeofence instance = entityManager.find(DeviceGeofence.class, id);
			log.debug("get successful");
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}
}
