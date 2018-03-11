package org.jeets.ear.ejb;

import javax.ejb.Singleton;
import javax.ejb.Startup;

@Singleton
@Startup
public class ApplicationBean implements JeetsApplication {

    @Override
    public void processMessage(String message) {
        System.out.println("JeetsApplication.processMessage(" + message + ")");
        
    }

}
