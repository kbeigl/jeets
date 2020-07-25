package org.jeets.etl.steps;

import org.apache.camel.AggregationStrategy;
import org.jeets.model.traccar.jpa.Position;

//import com.google.code.geocoder.model.GeocoderStatus;
import org.apache.camel.component.geocoder.GeocoderStatus;

public class GeocodeEnricher {
    
    /**
     * The new Exchange returns the geocoded address information which is set on
     * the original Exchange being a Position Object.
     */
    public static AggregationStrategy setAddress() {
//      
        return (original, newExchange) -> { // Java 8 !
            if (newExchange == null)        // possible with .enrich ?
                return null;
            /*
             * with cast:
             * GeocodeResponse address = (GeocodeResponse) newExchange.getIn().getBody();
             * with headers: {
             * breadcrumbId=ID-lenoxx-53518-1501932664269-0-1,
             * CamelGeoCoderAddress=Obere Regenstraße 8, 93059 Regensburg, Germany, 
             * CamelGeoCoderCity=Regensburg, 
             * CamelGeoCoderCountryLong=Germany, 
             * CamelGeoCoderCountryShort=DE, 
             * CamelGeoCoderLat=49.0312, CamelGeoCoderLatlng=49.0312,12.10331, CamelGeoCoderLng=12.10331, 
             * CamelGeoCoderStatus=OK}  
             */
            GeocoderStatus status = (GeocoderStatus) newExchange.getIn().getHeader("CamelGeoCoderStatus");
            if (status != GeocoderStatus.OK) {
                System.err.println("Problem with Geocoder " + status + " => no address added.");
            }
            else {
                Position pos = (Position) original.getIn().getBody();
                pos.setAddress((String) newExchange.getIn().getHeader("CamelGeoCoderAddress"));
            }
            return original;
        };
    }

}
