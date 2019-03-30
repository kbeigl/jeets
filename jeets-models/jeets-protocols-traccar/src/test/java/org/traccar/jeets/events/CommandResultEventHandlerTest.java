package org.traccar.jeets.events;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Map;

import org.junit.Test;
import org.traccar.BaseTest;
import org.traccar.jeets.events.CommandResultEventHandler;
import org.traccar.jeets.model.Position;
import org.traccar.model.Event;

public class CommandResultEventHandlerTest extends BaseTest {

    @Test
    public void testCommandResultEventHandler() throws Exception {
        
        CommandResultEventHandler commandResultEventHandler = new CommandResultEventHandler();
        
        Position position = new Position();
        position.set(Position.KEY_RESULT, "Test Result");
        Map<Event, Position> events = commandResultEventHandler.analyzePosition(position);
        assertNotNull(events);
        Event event = events.keySet().iterator().next();
        assertEquals(Event.TYPE_COMMAND_RESULT, event.getType());
    }

}
