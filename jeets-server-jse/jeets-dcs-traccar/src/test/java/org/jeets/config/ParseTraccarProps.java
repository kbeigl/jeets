package org.jeets.config;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.xml.XPathBuilder;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Traccar Properties Parser
 * <p>
 * parse the original Traccar configuration files and return a Properties object
 * with all '&lt;protocolname&gt;.port' keys.
 * <p>
 * The initial path and filename must be fed into this Route with<br>
 * from("file:setup/?fileName=traccar.xml&noop=true")<br>
 * Then the file is parsed for &lt;entry
 * key='config.default'&gt;value'&lt;/entry'&gt; to load the default
 * configuration. A Properties object with protocols and ports is generated from
 * both files. The Properties are set in the header 'properties' of the Exchange
 * which can be retrieved from("direct:traccar.properties").
 * <p>
 * hint: This Route can easily be extended to parse all traccar xml properties.
 *
 * @author kbeigl@jeets.org
 */
public class ParseTraccarProps extends RouteBuilder {

    /**
     * Base directory required for relative path of default.xml
     */
//  hardcoded. should be set in environment or system props
//  final static String TRACCAR_HOME = "F:/virtex/github.traccar/";
//  final static String TRACCAR_HOME = "F:/virtex/jeets-beta/traccar-etl/";
//  default for mvn test
    public static final String TRACCAR_HOME = "";
//  source
    private final String configKey  = "config.default";
//  target header name
    private final String properties = "properties";
//  temporary variable
    private final String traccarProps = "traccar.properties";

    @Override
    public void configure() throws Exception {

//      inside method or as member?
        Processor xmlToProps = new XmlToPropsProcessor();

//      expecting traccar.xml file as input
        from("direct:traccar.xml")
        .log("loading ${file:absolute.path}")

//      parse traccar props to header.properties
        .process(xmlToProps)

//      temporarily conserve result
        .setHeader(traccarProps, simple("${header." + properties + "}", Properties.class))
//      .setHeader(traccarProps, simple("header.properties", Properties.class))

//      get configKey
        .setHeader(configKey, xpath("//entry[@key='" + configKey + "']", String.class))
        .log("created header." + configKey  + "=${header." + configKey + "}")

//      compose file:uri
        .process(new Processor() {
            public void process(Exchange exchange) throws Exception {
                String defaultConfig = (String) exchange.getIn().getHeader(configKey);
//              catch if header configKey == null ...
                Path defaultPath = Paths.get(defaultConfig);
                String file = defaultPath.getFileName().toString();
                String path = defaultPath.getParent().toString().replace("\\", "/");
                String uri  = "file:" + TRACCAR_HOME + path + "?fileName=" + file + "&noop=true";
                exchange.getIn().setHeader("uri", uri);
            }
        })
        .log("header.uri=${header.uri}")

//      load specified file
        .pollEnrich().simple("${header.uri}")

//      transfer traccarProps from old to new Exchange
        .aggregationStrategy(new AggregationStrategy() {
            @Override
            public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
                newExchange.getIn().setHeader(traccarProps, oldExchange.getIn().getHeader(traccarProps));
                return newExchange;
            }
        })

//      parse default props to header.properties
        .process(xmlToProps)

//      now we can override the default values with customized traccar values
        .process(new Processor() {
            public void process(Exchange exchange) throws Exception {
                Properties traccar = exchange.getIn().getHeader(traccarProps, Properties.class);
                exchange.getIn().getHeader(properties, Properties.class).putAll(traccar);
            }
        })

//      return result for further processing
        .to("direct:traccar.properties");

    }

    /**
     * Get all &lt;entry&gt; nodes where attribute key contains '*.port' and store
     * (key,val) in Header 'properties' as Properties() object.
     */
    private class XmlToPropsProcessor implements Processor {

        @Override
        public void process(Exchange exchange) throws Exception {

            Properties props = new Properties();

            NodeList nodes = XPathBuilder.xpath("//entry[contains(@key,'.port')]").evaluate(exchange, NodeList.class);
            for (int i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);
//              System.out.println("<" + node.getNodeName() + ">");
                String key = node.getAttributes().getNamedItem("key").getNodeValue();
                String val = node.getTextContent();
                props.setProperty(key, val);
            }

            exchange.getIn().setHeader(ParseTraccarProps.this.properties, props);
        }
    }

}
