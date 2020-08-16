package org.jeets.dcs.traccar;

import java.util.Map;

import org.apache.camel.Consume;
import org.springframework.stereotype.Component;
import org.traccar.model.Position;

import ${package}.Loader;

/**
 * ETL component (Extract Transform and Load) to consume from Traccar DCS
 * (extract, transform) and load incoming Position messages to the System.
 * <p>
 * Currently this class is used to hide middle ware from the Loader
 * implementation. Besides calling the Loader this class can be used to
 * implement Enterprise Integration Patterns for additional message routing. For
 * example you could apply content based routers, splitters, aggregators to send
 * (filtered) messages to a message broker, a database and to the webfronend for
 * visual live tracking.
 * <p>
 * As a rule of thumb the proprietary code to the system should be implemented
 * in your Loader. This way you can easily upgrade my-etl-traccar and place the
 * artifact. in your existing environment.
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

    }

}
