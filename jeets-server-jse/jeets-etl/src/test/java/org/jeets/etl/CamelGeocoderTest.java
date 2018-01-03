package org.jeets.etl;

import java.util.List;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.jeets.etl.steps.GeocodeEnricher;
import org.jeets.model.traccar.jpa.Device;
import org.jeets.model.traccar.jpa.Position;
import org.jeets.model.traccar.util.Samples;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CamelGeocoderTest extends CamelTestSupport {
//    CamelSpringTestSupport

    private static final Logger LOG = LoggerFactory.getLogger(CamelGeocoderTest.class);

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
//              @formatter:off
                from("direct:jeets-dcs")    // represents from("seda:jeets-dcs")    
                .log("Received Device Entity '${body.name}' "               // body = device
/* 
 * This expression "... ${body.positions.size} positions."
 * after switching Device.positions from Set to List (ArrayList)
 * Line 49: Caused by: org.apache.camel.language.bean.RuntimeBeanExpressionException: 
 *             Failed to invoke method: size on null due to: 
 *             org.apache.camel.component.bean.AmbiguousMethodCallException: 
 *                Ambiguous method invocations possible: 
 *                   [public abstract int java.util.List.size(), 
 *                    public abstract int java.util.AbstractCollection.size()]. Exchange[]
 */
                        )

                .split(simple("${body.positions}"))
                    .log("Split line Position at (${body.latitude},${body.longitude})")
                    .enrich("direct:geocode", GeocodeEnricher.setAddress())   
                    .log("added new Address \"${body.address}\"")
                .end()

                .to("mock:persist");        // represents to("jpa:?usePersist=true")

                from("direct:geocode")
                .toD("geocoder:latlng:${body.latitude},${body.longitude}")  // body = position
                .log("Location ${header.CamelGeocoderAddress} "
                        + "is at ${header.CamelGeocoderLatlng} "
                        + "in ${header.CamelGeoCoderCountryShort}");
//              @formatter:on
            }
        };
    }

    @Test
    public void testMock() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:persist");
        mock.expectedMessageCount(1);

        Device device = Samples.createDeviceWithTwoPositions();
        device.setUniqueid(Samples.unique);
        device.setName("TestDevice");

        template.sendBody("direct:jeets-dcs", device);

//      TODO: assert new addresses
        List<Position> positions = device.getPositions();
        for (Position position : positions) {
            LOG.info(position.getAddress());
        }
        mock.assertIsSatisfied();
    }

}
