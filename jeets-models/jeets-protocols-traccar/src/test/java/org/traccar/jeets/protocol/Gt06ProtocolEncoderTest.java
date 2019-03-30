package org.traccar.jeets.protocol;

import org.junit.Test;
import org.traccar.jeets.ProtocolTest;
import org.traccar.jeets.protocol.Gt06ProtocolEncoder;
import org.traccar.model.Command;

public class Gt06ProtocolEncoderTest extends ProtocolTest {

    @Test
    public void testEncode() throws Exception {

        Gt06ProtocolEncoder encoder = new Gt06ProtocolEncoder();
        
        Command command = new Command();
        command.setDeviceId(1);
        command.setType(Command.TYPE_ENGINE_STOP);

        verifyCommand(encoder, command, binary("787812800c0000000052656c61792c312300009dee0d0a"));

    }

}
