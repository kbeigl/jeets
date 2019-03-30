package org.traccar;

import org.traccar.jeets.Context;

public class BaseTest {
    
    static {
        Context.init(new TestIdentityManager());
    }

}
