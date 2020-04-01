package org.jeets.itests;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * device2gts will be split in two ITests: 
 * device2traccarProtocols and device2wirelessLink
 * and a third test will be added: device2traccarDcs
 * The device2traccar* ITests should be identical to
 * test in two stages from traccar to jeets architecture.
 * Currently traccar protocols are launched but no messages are sent.
 */

public class Device2WirelessServerIT extends CamelTestSupport {

    private static final Logger LOG = LoggerFactory.getLogger(Device2WirelessServerIT.class);

//  TODO: externalize path definition, hard coded to my PC !! replace ABSOLUTE path
    private final String   repoFolder = "file://C:/kris/virtex/github.jeets/";
    private final String  soureFolder = repoFolder + "jeets-data/device.send?noop=true";
    private final String targetFolder = repoFolder + "jeets-clients/jeets-device/send";

    @Test
    public void testWireless() throws Exception {

//      add test with TCPClient send ..
        
        String fileName = "wireless-login.jdev";
        context.addRoutes(new RouteBuilder() {
            public void configure() throws Exception {
//              start test by copying file in send folder
                from( soureFolder + "&fileName=" + fileName )
                 .to( targetFolder);
//              pick up the device output (if any?)
//                from("direct:device.out.hex").to("mock:result");
            }
        });
        Thread.sleep(10000); // required > use NotifyBuilder ?
    }

}
