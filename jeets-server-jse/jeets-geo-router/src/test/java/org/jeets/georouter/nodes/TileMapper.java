package org.jeets.georouter.nodes;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.jeets.model.traccar.jpa.Device;
import org.jeets.model.traccar.jpa.Position;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Example for a TileRouter
 * see wiki.openstreetmap.org/wiki/Slippy_map_tilenames
 * 
 * @author kbeigl@jeets.org
 */
public class TileMapper implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(TileMapper.class);

    @Override
    public void process(Exchange exchange) throws Exception {
        Device device = (Device) exchange.getIn().getBody();

        Set<Position> positions = device.getPositions();        
        LOG.info("received jpa.Device {} with {} positions.", 
                device.getUniqueid(), positions.size());
        
        Position position = (Position) positions.toArray()[ positions.size()-1 ];
        LOG.info("map last position ({},{}) to tiles ..", position.getLatitude(), position.getLongitude());
        List<String> tileRecipients = new ArrayList<String>();
//      tile area covers 13 - village to 16 - small road
//      see wiki.openstreetmap.org/wiki/Zoom_levels
        for (int zoom = 13; zoom < 17; zoom++) {
            String tileString = getTileString(position.getLatitude(), position.getLongitude(), zoom);
//          LOG.info("tileString {}", tileString);
            exchange.getIn().setHeader("tileZ" + zoom, tileString);
            tileRecipients.add(tileString);
        }
        
//      tileRecipients toArray:
        String component = "activemq:";
        String recipientArray = 
                component + "z13x4371y2812, " +
                component + "z14x8742y5624, " +
                component + "z15x17485y11248, " +
                component + "z16x34971y22497";
        
        exchange.getIn().setHeader("tileRecipients", recipientArray);
    }

    /**
     * copied from wiki.openstreetmap.org/wiki/Slippy_map_tilenames#Java
     */
    public static String getTileString(final double lat, final double lon, final int zoom) {
        int xtile = (int) Math.floor((lon + 180) / 360 * (1 << zoom));
        int ytile = (int) Math.floor((1 - Math.log(Math.tan(Math.toRadians(lat)) + 1 
                / Math.cos(Math.toRadians(lat))) / Math.PI) / 2 * (1 << zoom));
        if (xtile < 0)
            xtile = 0;
        if (xtile >= (1 << zoom))
            xtile = ((1 << zoom) - 1);
        if (ytile < 0)
            ytile = 0;
        if (ytile >= (1 << zoom))
            ytile = ((1 << zoom) - 1);
//      return ("x" + xtile + "y" + ytile);
        return ("z" + zoom + "x" + xtile + "y" + ytile);
    }

}
