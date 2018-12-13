package org.jeets.dcs;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.jeets.model.traccar.jpa.Device;
import org.jeets.protocol.Traccar;

public class AckResponder implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        Device devEntity = (Device) exchange.getIn().getBody(Device.class);
//      LOG.info("AckResponder.process getIn jpa.Device.uniqueid " + devEntity.getUniqueid());
//      LOG.info("DcsProcessor received Device: {} at {}", devProto.getUniqueid(), new Date().getTime());

//      TODO: validate transformation
//      if (devEntity.getPositions().size() == devProto.getPositionCount()) // etc.
//      	LOG.info("validation OK at {}", new Date().getTime());
//      else
//          message received, validation failed > NoACK NAK (?)

        Traccar.Acknowledge.Builder ackBuilder = Traccar.Acknowledge.newBuilder();
//      ackBuilder.setDeviceid(devEntity.getUniqueid());
        ackBuilder.setDeviceid(789);
        exchange.getOut().setBody(ackBuilder.build(), Traccar.Acknowledge.class);
    }

}
