package org.jeets;

import java.io.File;
import java.io.FileOutputStream;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.converter.IOConverter;
import org.apache.camel.test.AvailablePortFinder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Device2gtsIT extends CamelTestSupport {

    private static final Logger LOG = LoggerFactory.getLogger(Device2gtsIT.class);
    
    private final String  soureFolder = "file:src/test/resources/data/?noop=true";
//  TODO: replace ABSOLUTE path
    private final String targetFolder = "file://C:/kris/virtex/github.jeets/jeets-clients/jeets-device/send";

//  apply AvailablePortFinder port below

    @Test
    public void testTwoServers() throws Exception {
            
        String fileName= "TwoServers.txt";
        
//      move down?
//      NotifyBuilder notify = new NotifyBuilder(context).whenDone(4).create();

        context.addRoutes(new RouteBuilder() {
            public void configure() throws Exception {
//              start test by copying file in send folder
                from( soureFolder + "&fileName=" + fileName )
                 .to( targetFolder);
//              pick up the device output (if any?)
//                from("direct:device.out.hex").to("mock:result");
            }
        });
        
//      check if files exist in send > sent > sending > sum up
//        File target = new File("target/outbox/hello.txt");
//        assertTrue("File not moved", target.exists());
//      validate against existing headers !

//      MockEndpoint mock = getMockEndpoint("mock:result");

//      assertTrue(notify.matchesMockWaitTime());
//      exact wait instead of 
        Thread.sleep(10000);
        
//      mock.expectedMessageCount(4);
//      mock.expectedMinimumMessageCount(1);

//      List<Exchange> messages = mock.getExchanges();
//      LOG.info("received " + messages.size() + " exchanges");
//      printExchangeList(messages);
        
//      assertMockEndpointsSatisfied();
        
//      assert hex <> expected ACK für req_resp
//      assert hex != Excception && hex = out

        LOG.info("... end " + fileName);
    }

    public void setUp() throws Exception {
//      conflict with xml: Route: file-in started and consuming from: file://send (creates send dir !)
//      works in the end ;)
        deleteDirectory("send");
        super.setUp();
    }
    
    private static volatile int port;

    @BeforeClass
    public static void initPort() throws Exception {
        File file = new File("target/nettyport.txt");

        if (!file.exists()) {
            // start from somewhere in the 25xxx range
//          port = AvailablePortFinder.getNextAvailable(25000);
            port = AvailablePortFinder.getNextAvailable();
        } else {
            // read port number from file
            String s = IOConverter.toString(file, null);
            port = Integer.parseInt(s);
            // use next free port
//          port = AvailablePortFinder.getNextAvailable(port + 1);
            port = AvailablePortFinder.getNextAvailable();
        }

    }

    @AfterClass
    public static void savePort() throws Exception {
        File file = new File("target/nettyport.txt");

        // save to file, do not append
        FileOutputStream fos = new FileOutputStream(file, false);
        try {
            fos.write(String.valueOf(port).getBytes());
        } finally {
            fos.close();
        }
    }

}
