package org.jeets.itests;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.properties.PropertiesComponent;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class Device2WirelessServerIT extends CamelTestSupport {

  private String sendFolder, dataFolder;

  @Test
  public void testWireless() throws Exception {

    // add test with TCPClient send ..

    String fileName = "wireless-login.jdev";
    context.addRoutes(
        new RouteBuilder() {
          public void configure() throws Exception {
            from(dataFolder + "&fileName=" + fileName).to(sendFolder);
          }
        });
    Thread.sleep(5 * 1000); // required
  }

  @Override
  protected CamelContext createCamelContext() throws Exception {
    CamelContext context = super.createCamelContext();
    PropertiesComponent props = (PropertiesComponent) context.getPropertiesComponent(); // Camel 3
    props.setLocation("classpath:folders.properties");
    sendFolder = "file://" + context.resolvePropertyPlaceholders("{{device.send.folder}}");
    dataFolder =
        "file://" + context.resolvePropertyPlaceholders("{{data.send.folder}}" + "?noop=true");
    // sentFolder = sendFolder + "/.sending/.sent/";
    return context;
  }
}
