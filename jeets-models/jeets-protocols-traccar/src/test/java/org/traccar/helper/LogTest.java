package org.traccar.helper;

import org.junit.Test;
import org.traccar.jeets.helper.Log;

import static org.junit.Assert.assertEquals;

public class LogTest {
    
    @Test
    public void testLog() {
        assertEquals("test - Exception (LogTest:12 < ...)", Log.exceptionStack(new Exception("test")));
    }

}
