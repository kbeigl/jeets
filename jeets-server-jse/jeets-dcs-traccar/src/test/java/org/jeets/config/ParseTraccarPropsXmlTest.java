package org.jeets.config;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class ParseTraccarPropsXmlTest extends CamelTestSupport {

/* 
 * org.apache.camel.TypeConversionException: Error during type conversion from type: java.lang.String to the required type: 
 * org.w3c.dom.Document with value [Body is instance of java.io.InputStream] due org.xml.sax.SAXParseException; lineNumber: 3; columnNumber: 70; 
 * Externe DTD: Lesen von externer DTD "properties.dtd" nicht erfolgreich, da "http"-Zugriff wegen der von der Eigenschaft 
 * "accessExternalDTD" festgelegten Einschränkung nicht zulässig ist.
 * solution: stackoverflow.com/questions/31293624/error-while-unmarshal-an-xml-with-jaxb-coused-by-dtd-file
 * TODO: find the Camel way to set unmarshaller properties
 */
    @Test
    public void testPortParser() throws Exception {

        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("file:setup/?fileName=traccar.xml&noop=true")
                .log("reading file ${header.CamelFileName}")
                .to("direct:xml");
            }
        });
        context.start();
        Thread.sleep(5000);
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {

        return new RouteBuilder() {

            public void configure() throws Exception {
//              context.setTracing(true);

                from("direct:xml")
//              add Route Names
                .split(xpath("/properties")) // root element
                    .log("${body}")
                    .multicast() // to be decided (?)
                        .to("direct:protocol")
                .end();

                from("direct:protocol")
                .log("Split by entry Element")
                .split(xpath("/properties/entry"))
                    .log("${body}")
                .end();

//            .to("");
//            register *Decoders and
//                dynamically create netty4:protocolname route ..
            }
        };
    }
}
