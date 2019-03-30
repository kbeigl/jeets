package org.traccar.jeets.protocol;

import org.junit.Test;
import org.traccar.jeets.ProtocolTest;
import org.traccar.jeets.protocol.CastelProtocolEncoder;
import org.traccar.model.Command;

public class CastelProtocolEncoderTest extends ProtocolTest {

    @Test
    public void testEncode() throws Exception {

        CastelProtocolEncoder encoder = new CastelProtocolEncoder();
        
        Command command = new Command();
        command.setDeviceId(1);
        command.setType(Command.TYPE_ENGINE_STOP);

        verifyCommand(encoder, command, binary("40402000013132333435363738393031323334350000000000458301a94a0d0a"));

    }

}
