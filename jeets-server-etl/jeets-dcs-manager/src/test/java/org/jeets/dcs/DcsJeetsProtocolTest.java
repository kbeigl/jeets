package org.jeets.dcs;

import org.apache.camel.CamelContext;
import org.apache.camel.component.properties.PropertiesComponent;
import org.apache.camel.spi.Registry;
import org.apache.camel.support.SimpleRegistry;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.jeets.protocol.JeetsProtocol;

public class DcsJeetsProtocolTest extends CamelTestSupport { // no spring

  // @Test
  public void testDcsRoute() throws Exception {
    /* see git history to recreate test with processor */
  }

  @Override
  protected Registry createCamelRegistry() throws Exception {
    Registry registry = new SimpleRegistry();
    // registry.bind("{{dcs.protobuffer.protocol}}", new DeviceProtoExtractor(null));    //
    // request  to server
    registry.bind("protobuffer", new JeetsProtocol(null)); // request  to server
    // registry.bind("ack", new ClientAckProtoExtractor(null));    // response to client
    return registry;
  }

  protected CamelContext createCamelContext() throws Exception {
    CamelContext context = super.createCamelContext();

    // temporarily for Camel 2 backward compatibility
    // see https://camel.apache.org/components/latest/file-component.html
    context.setLoadTypeConverters(true);

    PropertiesComponent props = (PropertiesComponent) context.getPropertiesComponent();
    props.setLocation("classpath:dcs.properties");
    // TODO: validate registering as "properties", advantages?
    // context.addComponent("properties", props);

    return context;
  }
}
