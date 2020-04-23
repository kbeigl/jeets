package org.jeets.dcs.init;

import org.apache.camel.main.Main;
import org.apache.camel.main.MainListenerSupport;
import org.apache.camel.main.MainSupport;
import org.jeets.dcs.init.routes.ConsumerDslRoute;
import org.jeets.dcs.init.routes.DcsRoutesFactory;
import org.jeets.dcs.init.routes.DcsRoutesFactoryDslRoute;
import org.jeets.dcs.init.tracking.TrackingSystem;

/**
 * Main class to initialize static traccar.Context, create and run DC servers on
 * all (available) ports. Shutdown with CTRL + C
 *
 * @author kbeigl@jeets.org
 */
public class DcsMain extends Main {

    public static void main(String[] args) throws Exception {
        DcsMain main = new DcsMain();
//      name irrelevant, looked up by Type?
        main.bind("dcsRoutesFactory", new DcsRoutesFactory());
        main.addRouteBuilder(new DcsRoutesFactoryDslRoute());
//      consume DCS messages
        main.addRouteBuilder(new ConsumerDslRoute());
        main.bind("trackingSystem", new TrackingSystem());

        // add event listener
//      main.addMainListener(new Events());
        // set the properties from a file
//      main.setPropertyPlaceholderLocations("example.properties");
        System.out.println("Starting Camel. Use CTRL + C to terminate the JVM.");
        main.run(args); // see built in args below
    }

    /*
    Apache Camel Runner takes the following options
    -h or -help = Displays the help screen
    -r or -routers <routerBuilderClasses> = Sets the router builder classes
                      which will be loaded while starting the camel context
    -d or -duration <duration> = Sets the time duration (seconds)
                      that the application will run for before terminating.
    -dm or -durationMaxMessages <durationMaxMessages> = Sets the duration of maximum number of messages
                      that the application will process before terminating.
    -di or -durationIdle <durationIdle> = Sets the idle time duration (seconds) duration
                      that the application can be idle before terminating.
    -t or -trace = Enables tracing
    -e or -exitcode <exitcode> = Sets the exit code if duration was hit
    -watch or -fileWatch <fileWatch> = Sets a directory to watch
                      for file changes to trigger reloading routes on-the-fly

    example, startup and run for 20 seconds:
    java -jar jeets-dcs-traccar-4.2.1-beta-jar-with-dependencies.jar -d 20
    */

    /* currently not applied
    public static class Events extends MainListenerSupport {
        @Override
        public void afterStart(MainSupport main) {
            System.out.println("DcsMain is now started!");
        }
        @Override
        public void beforeStop(MainSupport main) {
            System.out.println("DcsMain is now being stopped!");
        }
    }
     */
}
