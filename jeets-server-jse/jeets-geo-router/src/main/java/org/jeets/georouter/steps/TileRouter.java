package org.jeets.georouter.steps;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

/**
 * Example for a TileRouter
 * see wiki.openstreetmap.org/wiki/Slippy\_map\_tilenames
 * (lon,lat) to tile numbers
 * n = 2 ^ zoom
 * xtile = n * ((lon_deg + 180) / 360)
 * ytile = n * (1 - (log(tan(lat_rad) + sec(lat_rad)) / pi)) / 2
 * 
 * @author kbeigl
 */
public class TileRouter implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        // TODO Auto-generated method stub
        
    }

}
