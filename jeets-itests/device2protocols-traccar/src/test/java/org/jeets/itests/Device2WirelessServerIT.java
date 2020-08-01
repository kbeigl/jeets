package org.jeets.itests;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.properties.PropertiesComponent;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class Device2WirelessServerIT extends CamelTestSupport {

    private final String   repoFolder = "file://C:/kris/virtex/github.jeets/";
    private final String  soureFolder = repoFolder + "jeets-data/device.send?noop=true";
    private final String targetFolder = repoFolder + "jeets-clients/jeets-device/send";

//  TODO: externalize path definition, hard coded to my PC !! replace ABSOLUTE path
    private String sendFolder, sentFolder;

    @Test
    public void testWireless() throws Exception {

//      add test with TCPClient send ..
        
        String fileName = "wireless-login.jdev";
        context.addRoutes(new RouteBuilder() {
            public void configure() throws Exception {
                from( soureFolder + "&fileName=" + fileName )
                .to( targetFolder);
            }
        });
        Thread.sleep(10000); // required
    }

    @Override
    protected CamelContext createCamelContext() throws Exception {
        CamelContext context = super.createCamelContext();
        PropertiesComponent props = (PropertiesComponent) context.getPropertiesComponent();        // Camel 3  
        props.setLocation("classpath:folders.properties");
        sendFolder = context.resolvePropertyPlaceholders("{{device.send.folder}}");
        sentFolder = sendFolder + "/.sending/.sent/";
        return context;
    }

}
