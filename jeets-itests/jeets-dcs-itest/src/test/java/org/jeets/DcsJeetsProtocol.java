package org.jeets;

import java.util.Date;
import java.util.List;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.component.properties.PropertiesComponent;
import org.apache.camel.spi.Registry;
import org.apache.camel.support.SimpleRegistry;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.jeets.model.traccar.jpa.Device;
import org.jeets.model.traccar.jpa.Position;
import org.jeets.protocol.JeetsProtocol;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DcsJeetsProtocol extends CamelTestSupport {

    /*
     * This test is not an IT and should be moved to dcs or protocols.
     * Currently is has its own dcs.properties > generalize
     * Then add IT for explicit JeetsProtocol with Camel ACK
     */

    private static final Logger LOG = LoggerFactory.getLogger(DcsJeetsProtocol.class);

//    @Test
    public void testDcsRoute() throws Exception {

//      currently no Producer for Consumer Endpoint
//      make it a SpringBootTest with DCS ?
        context.addRoutes(new RouteBuilder() {
            public void configure() throws Exception {
                from("direct:traccar.model") // "seda:jeets-dcs"
                .process(new Processor() {
                    public void process(Exchange exchange) throws Exception {
                        Object msg = exchange.getIn().getBody();
                        LOG.info("received message: {}", msg);
                        Device device = (Device) msg;
                        LOG.info("received jpa.Device {} with {} positions.", 
                                device.getUniqueid(), device.getPositions().size());
                   }
                })
                .to("mock:result");
            }
        });
        MockEndpoint mock = getMockEndpoint("mock:result");

//      messages from jeets-tracker main
//      6 device messages can be sent in packages of 1, 2, ..
//      wait for 2 messages, validate and finish test
        mock.expectedMessageCount(2);
//      mock.expectedMinimumMessageCount(1);
        assertMockEndpointsSatisfied();

        validateMessages(mock.getExchanges());
    }

    private void validateMessages(List<Exchange> exchanges) {
        LOG.info("evaluate {} received exchanges,", exchanges.size());

        for (int nr = 0; nr < exchanges.size(); nr++) {
//          assert instanceOf Device
            Device dev = (Device) exchanges.get(nr).getIn().getBody(); 
            LOG.info("  Device#{}  sent:{} received:{}", nr, dev.getLastupdate(), new Date());
//          inside IT message should be sent in the last ten minutes - keep it safe
            assertEquals(0, (new Date().getTime() - dev.getLastupdate().getTime()), 10*60*1000); 
            List<Position> devPositions = dev.getPositions();
            Date pos0fix = devPositions.get(0).getFixtime();
            for (int pos = 1; pos < devPositions.size(); pos++) {
                LOG.info("Position#{} fixed:{}   server:{}", pos, devPositions.get(pos).getFixtime(), devPositions.get(pos).getServertime());
                assertTrue("fixtimes are not in chronological order!", pos0fix.before(devPositions.get(pos).getFixtime()));
                pos0fix = devPositions.get(pos).getFixtime();
            }
/*          inspect: tracker log, DevBuilder, msg queue, nr of positions against this test ..
            [main] INFO org.jeets.DcsIT - received 2 exchanges,
            [main] INFO org.jeets.DcsIT -    Device#0 sent:Wed Oct 10 11:53:47 CEST 2018 received:Wed Oct 10 11:53:47 CEST 2018
            [main] INFO org.jeets.DcsIT - Position#0 fixed:Wed Oct ***11:53:12***   2018   server:Wed Oct 10 11:53:47 CEST 2018
            [main] INFO org.jeets.DcsIT - Position#1 fixed:Wed Oct ***11:53:22***   2018   server:Wed Oct 10 11:53:47 CEST 2018
            [main] INFO org.jeets.DcsIT -    Device#1 sent:Wed Oct 10 11:53:47 CEST 2018 received:Wed Oct 10 11:53:47 CEST 2018
            [main] INFO org.jeets.DcsIT - Position#0 fixed:Wed Oct ***11:53:32***   2018   server:Wed Oct 10 11:53:47 CEST 2018
            [main] INFO org.jeets.DcsIT - Position#1 fixed:Wed Oct ***11:53:42***   2018   server:Wed Oct 10 11:53:47 CEST 2018 */
        }
    }

    @Override
    protected Registry createCamelRegistry() throws Exception {
        Registry registry = new SimpleRegistry();
//      registry.bind("{{dcs.protobuffer.protocol}}", new DeviceProtoExtractor(null));    // request  to server
        registry.bind("protobuffer", new JeetsProtocol(null));    // request  to server
//      registry.bind("ack", new ClientAckProtoExtractor(null));    // response to client
        return registry;
    }

//    @Override
//    protected RouteBuilder createRouteBuilder() throws Exception {
//        return new DcsRoute(); deprecated initial simple jeets-dcs
//    }

    protected CamelContext createCamelContext() throws Exception {  
        CamelContext context = super.createCamelContext();  

//      temporarily for Camel 2 backward compatibility
//      see https://camel.apache.org/components/latest/file-component.html
        context.setLoadTypeConverters(true);
        
        PropertiesComponent props = (PropertiesComponent) context.getPropertiesComponent();  
        props.setLocation("classpath:dcs.properties");
//      TODO: validate registering as "properties", advantages?
//      context.addComponent("properties", props);
  
        return context;  
    }  

}
