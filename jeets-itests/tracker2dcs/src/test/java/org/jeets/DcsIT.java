package org.jeets;

import java.util.Date;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.jeets.dcs.DcsRoute;
import org.jeets.dcs.steps.DeviceProtoExtractor;
import org.jeets.model.traccar.jpa.Device;
import org.jeets.model.traccar.jpa.Position;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DcsIT extends CamelTestSupport {

    private static final Logger LOG = LoggerFactory.getLogger(DcsIT.class);

    /**
     * Compare original DcsRouteTest in jeets-dcs
     * <p>
     * TODO: harmonize settings for host, port, messages!, device name (also for
     * queuing) etc. for tracker, dcs, dcsIT projects. <br>
     * http://camel.apache.org/using-propertyplaceholder.html
     * <p>
     * Actually the DCS is not treated as a Component. This test simply uses the
     * DcsRoute inside the test environment analogous to applying the DcsRoute
     * in any higher level module.
     * 
     * @throws Exception
     */
    @Test
    public void testDcsRoute() throws Exception {
        LOG.info("create DcsRoute ...");
        context.addRoutes(new DcsRoute() );
        context.addRoutes(new RouteBuilder() {
            public void configure() throws Exception {
                from("seda:jeets-dcs")
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
    protected JndiRegistry createRegistry() throws Exception {
        JndiRegistry registry = super.createRegistry();
        registry.bind("device", new DeviceProtoExtractor(null));
        return registry;
    }
    
//  @Test   initial IT to get goin'
    public void testExecute() throws Exception {
        System.out.println("testExecute");
        assertEquals(0, execute(new String[] {}));
        assertEquals(1, execute(new String[] { "one" }));
        assertEquals(6, execute(new String[] { "one", "two", "three", "four", "five", "six" }));
    }

    private int execute(String[] args) throws Exception {
        if (args != null)
            return args.length;
        else 
            return 0;
    }

}
