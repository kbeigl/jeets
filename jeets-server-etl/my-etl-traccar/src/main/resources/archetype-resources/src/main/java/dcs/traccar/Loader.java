#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import org.traccar.model.Position;

public class Loader {

    public void processPosition(Position position) {
        System.out.println("processPosition " + position);

        System.out.println("jdbc.lookup db id by IMEI ...");

        System.out.println("is db id registered ? ...");

        System.out.println("INSERT lat lon ... INTO waypoints");
    }

}
