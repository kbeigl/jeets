package org.jeets.ear.ejb;

/**
 * A few simple methods to comprise a GTS application.
 * 
 * @author kbeigl@jeets.org
 */
public interface JeetsApplication {
    
    /**
     * The entry point for GPS messages from different devices.
     */
    void processMessage(String message);

}
