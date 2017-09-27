package de.jeets.model.traccar.jpa.ejb;
// Generated 26.11.2016 12:59:23 by Hibernate Tools 5.2.0.Beta1

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jeets.model.traccar.jpa.Server;

/**
 * Home object for domain model class Server.
 * @see de.jeets.model.traccar.jpa.Server
 * @author Hibernate Tools
 */
@Stateless
public class ServerHome {

	private static final Log log = LogFactory.getLog(ServerHome.class);

	@PersistenceContext
	private EntityManager entityManager;

	public void persist(Server transientInstance) {
		log.debug("persisting Server instance");
		try {
			entityManager.persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void remove(Server persistentInstance) {
		log.debug("removing Server instance");
		try {
			entityManager.remove(persistentInstance);
			log.debug("remove successful");
		} catch (RuntimeException re) {
			log.error("remove failed", re);
			throw re;
		}
	}

	public Server merge(Server detachedInstance) {
		log.debug("merging Server instance");
		try {
			Server result = entityManager.merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public Server findById(int id) {
		log.debug("getting Server instance with id: " + id);
		try {
			Server instance = entityManager.find(Server.class, id);
			log.debug("get successful");
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}
}
