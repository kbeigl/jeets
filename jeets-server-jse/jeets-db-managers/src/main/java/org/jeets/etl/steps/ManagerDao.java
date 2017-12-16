package org.jeets.etl.steps;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.camel.Consume;
import org.apache.camel.Exchange;
import org.apache.camel.RecipientList;
import org.apache.camel.component.jpa.JpaConstants;
import org.jeets.model.traccar.jpa.Device;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

@Component
//@Transactional
public class ManagerDao {

    private static final Logger LOG = LoggerFactory.getLogger(ManagerDao.class);

    @PersistenceContext
    EntityManager em;

    @Consume(uri = "direct:managers.in")
    @RecipientList
    public String[] consume(Exchange exchange) {
//      if (1==1) {
        System.out.println(exchange);
        LOG.info("lookup Device ..");

        Device gtsDevice = null;
        Device inDevice = (Device) exchange.getIn().getBody();
        try {
            gtsDevice = lookupDevice(exchange, inDevice.getUniqueid());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println(gtsDevice);
//      ERROR
        System.out.println(gtsDevice.getPositions());
//        Caused by: org.hibernate.LazyInitializationException: 
//            failed to lazily initialize a collection of role: 
//                org.jeets.model.traccar.jpa.Device.positions, 
//                could not initialize proxy - no Session
        
        
        
        
        return new String[] {"direct:manager1.out"}; 
//                          ,"activemq:manager2.out"};
            
//        } else {
//            LOG.info("Person is from AMER region");
//            return new String[] {"file:target/messages/amer/hr_pickup",
//                                 "file:target/messages/amer/finance_pickup"};
//        }
    }

    /**
     * Currently a new Device is added if uniqueID doesn't exist in database.
     * This implies a Device registration and should be changed, since Traccar
     * Devices are registered via database (and webfrontend).
     */
//    @Transactional
    private Device lookupDevice(Exchange exchange, final String uniqueId) throws Exception {
//        TransactionTemplate transactionTemplate = exchange.getContext().getRegistry()
//                .lookupByNameAndType("transactionTemplate", TransactionTemplate.class);
//
//        return transactionTemplate.execute(new TransactionCallback<Device>() {
//            public Device doInTransaction(TransactionStatus status) {
//                em.joinTransaction();
//              TODO: return unique Device! not List.
//              When you made changes on the managed objects, you do not worry about merging 
//                as those changes will be picked up by the EntityManager automatically.
                List<Device> list = em.createNamedQuery("findDeviceByUniqueId", Device.class)
                        .setParameter("uniqueid", uniqueId).getResultList();
//                      .setParameter("uniqueid", uniqueId).getFirstResult();
//                      .setParameter("uniqueid", uniqueId).getSingleResult();
                Device dev;
                if (list.isEmpty()) {
//                  throw NoResultException
                    dev = new Device(); // or return null ..
                    dev.setUniqueid(uniqueId);
                    LOG.info("A new Device with uniqueID {} WILL BE REGISTERED.", uniqueId);
                } else {
                    dev = list.get(0);  // managed
                    LOG.info("Found a registered Device with uniqueId {}.", uniqueId);
                }
//              external check isManaged?
                System.out.println("found device: " + dev);
                return dev;
//            }
//        });
    }

}
