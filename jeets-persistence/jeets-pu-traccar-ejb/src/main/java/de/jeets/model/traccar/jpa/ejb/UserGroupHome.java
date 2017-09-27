package de.jeets.model.traccar.jpa.ejb;
// Generated 26.11.2016 12:59:23 by Hibernate Tools 5.2.0.Beta1

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jeets.model.traccar.jpa.UserGroup;
import de.jeets.model.traccar.jpa.UserGroupId;

/**
 * Home object for domain model class UserGroup.
 * @see de.jeets.model.traccar.jpa.UserGroup
 * @author Hibernate Tools
 */
@Stateless
public class UserGroupHome {

	private static final Log log = LogFactory.getLog(UserGroupHome.class);

	@PersistenceContext
	private EntityManager entityManager;

	public void persist(UserGroup transientInstance) {
		log.debug("persisting UserGroup instance");
		try {
			entityManager.persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void remove(UserGroup persistentInstance) {
		log.debug("removing UserGroup instance");
		try {
			entityManager.remove(persistentInstance);
			log.debug("remove successful");
		} catch (RuntimeException re) {
			log.error("remove failed", re);
			throw re;
		}
	}

	public UserGroup merge(UserGroup detachedInstance) {
		log.debug("merging UserGroup instance");
		try {
			UserGroup result = entityManager.merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public UserGroup findById(UserGroupId id) {
		log.debug("getting UserGroup instance with id: " + id);
		try {
			UserGroup instance = entityManager.find(UserGroup.class, id);
			log.debug("get successful");
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}
}
