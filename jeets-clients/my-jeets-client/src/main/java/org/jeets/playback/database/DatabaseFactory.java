package org.jeets.playback.database;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TemporalType;

import org.jeets.model.traccar.jpa.Position;

public class DatabaseFactory {

    /**
     * Create DatabaseFactory with connection parameters and create
     * EntityManager to connect to database.
     */
    public DatabaseFactory(String jdbcUrl, String persistenceUnit) {
        this.jdbcUrl = jdbcUrl;
        this.persistenceUnit = persistenceUnit;
        createEntityManager();
    }
    
//  default/sample values should be overridden in Constructor
    private String jdbcUrl = "jdbc:postgresql://localhost:5432/traccar3.14";
    private String persistenceUnit = "jeets-pu-traccar-jpa";

//  private String hbm2ddlAuto = "create"; // "create-drop";

//  TODO: Add Exception handling and throw to caller ..
    public List<Position> selectPositionList(String uniqueId, Date fromDate, Date toDate) {
//      throws ..

        createEntityManager();

        String sql = "select p from Position p " 
                + "left join fetch p.device d " 
                + "where d.uniqueid = :uniqueid "
                + "and p.fixtime between :from and :to " 
                + "order by p.fixtime";

        List<Position> positions = entityManager.createQuery(sql, org.jeets.model.traccar.jpa.Position.class)
                .setParameter("uniqueid", uniqueId).setParameter("from", fromDate, TemporalType.TIMESTAMP)
                .setParameter("to", toDate, TemporalType.TIMESTAMP).getResultList();
        // catch noResult (return null?)

        entityManager.close();
        entityManagerFactory.close();

        return positions;
    }

    private EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;

    private void createEntityManager() {
        Map<String, Object> overrideConfig = new HashMap<String, Object>();
        // choose your database
        overrideConfig.put("javax.persistence.jdbc.url", jdbcUrl);
        // overrideConfig.put("hibernate.hbm2ddl.auto", hbm2ddlAuto);
        entityManagerFactory = Persistence.createEntityManagerFactory(persistenceUnit, overrideConfig);
        // entityManagerFactory =
        // Persistence.createEntityManagerFactory(persistenceUnit);
        // override persistence properties as described in Book Sec 6.4.1 !!
//      System.out.println(entityManagerFactory.getProperties());
        entityManager = entityManagerFactory.createEntityManager();
    }

}
