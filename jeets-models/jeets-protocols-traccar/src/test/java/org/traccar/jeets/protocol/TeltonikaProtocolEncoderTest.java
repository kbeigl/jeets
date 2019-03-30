package org.traccar.jeets.protocol;

import org.junit.Test;
import org.traccar.jeets.ProtocolTest;
import org.traccar.jeets.protocol.TeltonikaProtocolEncoder;
import org.traccar.model.Command;

public class TeltonikaProtocolEncoderTest extends ProtocolTest {

    @Test
    public void testEncode() throws Exception {

        TeltonikaProtocolEncoder encoder = new TeltonikaProtocolEncoder();
        
        Command command = new Command();
        command.setDeviceId(1);
        command.setType(Command.TYPE_CUSTOM);
        command.set(Command.KEY_DATA, "setdigout 11");

        verifyCommand(encoder, command, binary("00000000000000160C01050000000E7365746469676F75742031310D0A010000E258"));

    }

}
