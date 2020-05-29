package org.jeets.config;

//import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.traccar.Context;

public class ParseTraccarPropsTest extends CamelTestSupport {

    // fos requires existing directory !
    private final String targetPath = "./src/test/resources/";
//    private final String TARGET_PATH = "./target/";

    @Test
    public void testPortParser() throws Exception {

//      ADD ASSERTIONS ;)

        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {

//              move this into Route?
                from("file:" + ParseTraccarProps.TRACCAR_HOME + "setup/?fileName=traccar.xml&noop=true")
//              from("file:{{env:TRACCAR_HOME}} ...
//              catch file not found ?
                .log("parsing file ${header.CamelFileName} for <protocolname>.port=port# ...")
                .to("direct:traccar.xml");

//              Route: ParseTraccarProps from traccar.xml to traccar.properties

//              pick up created props
                from("direct:traccar.properties")

//              now you can do whatever you want with the Properties()
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {

                        Properties traccarProps = exchange.getIn().getHeader("properties", Properties.class);
//                      System.out.println(traccarProps);

//                      .. properties to file with header
//                      # created by Traccar Property Parser at yymm ..
//                      # from xml sources at TRACCAR_HOME

//                      .. order by port asc

//                      OR use as standard solution ?
//                        traccarProps.load(inStream);
//                        traccarProps.loadFromXML(in);
//                        traccarProps.storeToXML(os, comment);
//                        traccarProps.store(out, comments);

                        storeProps(traccarProps);
                    }
                })

                .to("mock:props");
            }
        });
        context.start();
        Thread.sleep(5000);
    }

//  example with built-in Properties methods
    private void storeProps(Properties props) {
        try {

            FileOutputStream fosxml = new FileOutputStream(targetPath + "properties.xml");
            props.storeToXML(fosxml, "all protocol names and ports from Traccar xml configuration");

//            FileInputStream fisxml = new FileInputStream( TARGET_PATH + "properties.xml");
//            while (fisxml.available() > 0) {
//               System.out.print("" + (char) fisxml.read());
//            }

            FileOutputStream fos = new FileOutputStream(targetPath + "traccar.properties");
            props.store(fos, "all protocol names and ports from Traccar xml configuration");

//            FileInputStream fis = new FileInputStream( TARGET_PATH + "traccar.properties");
//            while (fis.available() > 0) {
//                System.out.print("" + (char) fis.read());
//             }

         } catch (IOException ex) {
             ex.printStackTrace();
         }
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new ParseTraccarProps();
    }

    @Test
    public void testContextInit() throws Exception {

        Context.init("./setup/traccar.xml");
        
        assertNotNull("Config was not loaded", Context.getConfig());
//      validate if default values are overridden
//      check if ports are available and other required props

        assertNotNull("DeviceManager was not loaded", Context.getDeviceManager());
        assertNotNull("IdentityManager was not loaded", Context.getIdentityManager());

        assertNotNull("MediaManager was not loaded", Context.getMediaManager());
        System.out.println("media.path: " + Context.getConfig().getString("media.path"));

        assertNotNull("ConnectionManager was not loaded", Context.getConnectionManager());
//      ServerManager should NOT be started (in addition to camel-netty)
        assertNull("ServerManager should NOT be loaded", Context.getServerManager());
    }

}
