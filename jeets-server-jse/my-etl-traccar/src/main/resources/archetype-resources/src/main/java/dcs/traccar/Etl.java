package org.jeets.dcs.traccar;

import java.util.Map;

import org.apache.camel.Consume;
import org.springframework.stereotype.Component;
import org.traccar.model.Position;

import ${package}.Loader;

/**
 * ETL component (Extract Transform and Load)
 * <p>
 * Consumes Traccar DCS (extract, transform) output (load)
 */
@Component
public class Etl {

	private Loader ptcLoader = new Loader();

    @Consume(uri = "direct:traccar.model")
    public void onPosition(Position position) {

        System.out.println("ETL received position: " + position);

        Map<String, Object> attribs = position.getAttributes();
        for (String key : attribs.keySet()) {
            System.out.println(key + ": " + attribs.get(key));
        }

		ptcLoader.processPosition(position);

        System.out.println("jdbc.lookup db id by IMEI ...");

        System.out.println("is db id registered ? ...");

        System.out.println("INSERT lat lon ... INTO waypoints");

    }

}
