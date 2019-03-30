package org.traccar.jeets.protocol;

import org.junit.Test;
import org.traccar.jeets.ProtocolTest;
import org.traccar.jeets.protocol.CityeasyProtocolEncoder;
import org.traccar.model.Command;

public class CityeasyProtocolEncoderTest extends ProtocolTest {

    @Test
    public void testEncode() throws Exception {

        CityeasyProtocolEncoder encoder = new CityeasyProtocolEncoder();

        Command command = new Command();
        command.setDeviceId(1);
        command.setType(Command.TYPE_SET_TIMEZONE);
        command.set(Command.KEY_TIMEZONE, "GMT+6");

        verifyCommand(encoder, command, binary("5353001100080001680000000B60820D0A"));

    }

}
