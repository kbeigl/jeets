package org.traccar.jeets.protocol;

import org.junit.Test;
import org.traccar.jeets.ProtocolTest;
import org.traccar.jeets.protocol.GalileoProtocolEncoder;
import org.traccar.model.Command;

public class GalileoProtocolEncoderTest extends ProtocolTest {

    @Test
    public void testEncode() throws Exception {

        GalileoProtocolEncoder encoder = new GalileoProtocolEncoder();

        Command command = new Command();
        command.setDeviceId(1);
        command.setType(Command.TYPE_CUSTOM);
        command.set(Command.KEY_DATA, "status");

        verifyCommand(encoder, command, binary("01200003313233343536373839303132333435040000e000000000e1067374617475731f64"));

    }

}
