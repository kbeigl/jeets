/**
 * Copyright 2017 The Java EE Tracking System - JeeTS
 * Copyright 2017 Kristof Beiglböck kbeigl@jeets.org
 *
 * The JeeTS Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.jeets.etl.steps;

import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;

import org.apache.camel.Converter;
import org.apache.camel.Exchange;
import org.apache.camel.component.jpa.JpaConstants;
import org.jeets.model.traccar.jpa.Device;
import org.jeets.model.traccar.jpa.Position;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Lookup Device transformed from Proto Device Message to Entity, add required
 * fields and return managed Entity for Persisting.
 */
@Converter
public final class DeviceEntityLookup {

    private static final Logger LOG = LoggerFactory.getLogger(DeviceEntityLookup.class);

    private DeviceEntityLookup() {
    }

    @Converter
    public static Device lookupDevice(NetworkDevice networkDevice, Exchange exchange) throws Exception {
//      unwrap Device
        Device netDevice = networkDevice.getDevice();
        String uniqueId = netDevice.getUniqueid();
        LOG.info("lookup incoming Device {} with {} positions and {} events.", 
                uniqueId, netDevice.getPositions().size(), netDevice.getEvents().size());
        
//      Note! This method will always return a Device, registered or not!
//      might as well return null ...
        Device dbDevice = findDeviceByUniqueId(exchange, uniqueId);
        if ( dbDevice.getId()== 0) {
//          unregistered, unmanaged new Device with only uniqueId
//          might as well use netDevice
            
//            TODO: UNTESTED YET
            
            dbDevice = netDevice;
//          add required fields (here?)
            dbDevice.setName("<unregistered>");
//          temporarily ignored ======================
            dbDevice.setEvents(null);
        } else {
//          use managed Device for persistence
//          only change related positions and events
            dbDevice.setPositions(netDevice.getPositions());
            for (Position position : dbDevice.getPositions()) {
                position.setDevice(dbDevice);
            }
//          detach already persisted events
            dbDevice.setEvents(null);
//          set by Transformer in DCS
            dbDevice.setLastupdate(netDevice.getLastupdate());
        }
//      order positions (with Collection etc.) and pick latest:
//      dbDevice.setPositionid(positionid);
//      currently the positionid is not being set and is therefore wrong !
        
        LOG.info("Lookup returns device entity at {}", new Date().getTime());
        return dbDevice;
    }

    /**
     * Currently a new Device is added if uniqueID doesn't exist in database.
     * This implies a Device registration and should be changed, since Traccar
     * Devices are registered via database (and webfrontend).
     */
    private static Device findDeviceByUniqueId(Exchange exchange, final String uniqueId) throws Exception {
        final EntityManager entityManager = exchange.getProperty(JpaConstants.ENTITY_MANAGER, EntityManager.class);
        TransactionTemplate transactionTemplate = exchange.getContext().getRegistry()
                .lookupByNameAndType("transactionTemplate", TransactionTemplate.class);

        return transactionTemplate.execute(new TransactionCallback<Device>() {
            public Device doInTransaction(TransactionStatus status) {
                entityManager.joinTransaction();
//              TODO: return unique Device! not List.
//              When you made changes on the managed objects, you do not worry about merging 
//                as those changes will be picked up by the EntityManager automatically.
                List<Device> list = entityManager.createNamedQuery("findDeviceByUniqueId", Device.class)
                        .setParameter("uniqueid", uniqueId).getResultList();
//                      .setParameter("uniqueid", uniqueId).getFirstResult();
//                      .setParameter("uniqueid", uniqueId).getSingleResult();
                Device dev;
//              TODO: make configurable (see Traccar .. )
                if (list.isEmpty()) {
                    dev = new Device(); // or return null ..
                    dev.setUniqueid(uniqueId);
                    LOG.info("A new Device with uniqueID {} WILL BE REGISTERED.", uniqueId);
                } else {
                    dev = list.get(0);
                    LOG.info("Found a registered Device with uniqueId {}.", uniqueId);
                }
                return dev;
            }
        });
    }

}
