package org.traccar.jeets.protocol;

import org.junit.Ignore;
import org.junit.Test;
import org.traccar.jeets.ProtocolTest;
import org.traccar.jeets.protocol.HuabaoProtocolEncoder;
import org.traccar.model.Command;

public class HuabaoProtocolEncoderTest extends ProtocolTest {

    @Ignore
    @Test
    public void testEncode() throws Exception {

        HuabaoProtocolEncoder encoder = new HuabaoProtocolEncoder();
        
        Command command = new Command();
        command.setDeviceId(1);
        command.setType(Command.TYPE_ENGINE_STOP);

        verifyCommand(encoder, command, binary("7EA0060007001403305278017701150424154610AD7E"));

    }

}
