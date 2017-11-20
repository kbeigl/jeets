package org.jeets.dcsToAmq;

import java.util.Date;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.jeets.model.traccar.jpa.Device;
import org.jeets.protocol.Traccar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AckResponder implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(AckResponder.class);

    @Override
    public void process(Exchange exchange) throws Exception {
        Device devEntity =  (Device) exchange.getIn().getBody(Device.class);
//      System.out.println("AckResponder.process getIn jpa.Device.uniqueid " + devEntity.getUniqueid());
        LOG.info("DcsProcessor received Device: {} at {}", devEntity, new Date().getTime());
/*
//      TODO: validate transformation
        if (devEntity.getPositions().size() == devProto.getPositionCount()) // etc.
            LOG.info("validation OK at {}", new Date().getTime());
//      else
//          message received, validation failed > NoACK NAK (?)
 */
        Traccar.Acknowledge.Builder ackBuilder = Traccar.Acknowledge.newBuilder();
//      ackBuilder.setDeviceid(devEntity.getUniqueid());
        ackBuilder.setDeviceid(789);
        exchange.getOut().setBody(ackBuilder.build(), Traccar.Acknowledge.class);
    }

}
