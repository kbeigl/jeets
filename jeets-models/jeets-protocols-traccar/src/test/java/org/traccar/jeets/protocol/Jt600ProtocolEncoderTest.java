package org.traccar.jeets.protocol;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.traccar.jeets.ProtocolTest;
import org.traccar.jeets.protocol.Jt600ProtocolEncoder;
import org.traccar.model.Command;

public class Jt600ProtocolEncoderTest extends ProtocolTest {
    Jt600ProtocolEncoder encoder = new Jt600ProtocolEncoder();
    Command command = new Command();

    @Test
    public void testEngineStop() throws Exception {
        command.setType(Command.TYPE_ENGINE_STOP);
        assertEquals("(S07,0)", encoder.encodeCommand(command));
    }

    @Test
    public void testEngineResume() throws Exception {
        command.setType(Command.TYPE_ENGINE_RESUME);
        assertEquals("(S07,1)", encoder.encodeCommand(command));
    }

    @Test
    public void testSetTimezone() throws Exception {
        command.setType(Command.TYPE_SET_TIMEZONE);
        command.set(Command.KEY_TIMEZONE, "GMT+4");
        assertEquals("(S09,1,240)", encoder.encodeCommand(command));
    }

    @Test
    public void testReboot() throws Exception {
        command.setType(Command.TYPE_REBOOT_DEVICE);
        assertEquals("(S17)", encoder.encodeCommand(command));
    }
}
