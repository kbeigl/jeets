/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jeets.camel.component.dcs;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.jeets.model.traccar.jpa.Device;
import org.jeets.protocol.Traccar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The DCS Producer dispatches Device Entities to the Component User. <br>
 * Producer, i.e. target endpoints produce requests and they always appears at the end of a route. <br>
 * From a service-oriented prospective a producer represents a service consumer.
 * Because it comes at the end of a processor pipeline, the producer is also a processor object!
 */
public class DcsProducer extends DefaultProducer {
    private static final Logger LOG = LoggerFactory.getLogger(DcsProducer.class);
    private DcsEndpoint endpoint;

    public DcsProducer(DcsEndpoint endpoint) {
        super(endpoint);
        System.out.println("new DcsProducer: " + endpoint);
        this.endpoint = endpoint;
    }
    
//  IMPLEMENT OUT ONLY PATTERN AND RETURN IN TO NETTY PIPELINE
//  i.e. make the Exchange out only! while returning Ack

    public void process(Exchange exchange) throws Exception {
//      validate type via cast (Acknowledge) (Device) (jpa..) ?
        System.out.println("DcsProducer.process getIn: " + exchange.getIn().getBody());

//      (cast) getIn automatically triggers Converter with in and out Types
        Device devEntity =  (Device) exchange.getIn().getBody(Device.class);
//      Traccar.Device devProto = exchange.getIn().getBody(Traccar.Device.class);
/*
        Traccar.Device devProto = (Traccar.Device) exchange.getIn().getBody();
        LOG.info("DcsProcessor received Device: {} at {}", devProto.getUniqueid(), new Date().getTime());
        
//      TODO: validate transformation
        if (devEntity.getPositions().size() == devProto.getPositionCount()) // etc.
            LOG.info("validation OK at {}", new Date().getTime());
//      else
//          message received, validation failed > NoACK NAK (?)
 */
        Traccar.Acknowledge.Builder ackBuilder = Traccar.Acknowledge.newBuilder();
//      ackBuilder.setDeviceid(devEntity.getUniqueid());
        ackBuilder.setDeviceid(789);
//      exchange.getOut().setBody(ackBuilder, Traccar.Acknowledge.class);
        exchange.getOut().setBody(ackBuilder.build(), Traccar.Acknowledge.class);
    }

}
