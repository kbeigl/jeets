/*
 * Copyright 2017 Anton Tananaev (anton@traccar.org)
 * Copyright 2017 Anatoliy Golubev (darth.naihil@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.traccar.jeets.protocol;

import org.junit.Test;
import org.traccar.jeets.ProtocolTest;
import org.traccar.jeets.protocol.AdmProtocolEncoder;
import org.traccar.model.Command;

import static org.junit.Assert.assertEquals;

public class AdmProtocolEncoderTest extends ProtocolTest {

    @Test
    public void testEncode() throws Exception {

        AdmProtocolEncoder encoder = new AdmProtocolEncoder();

        Command command = new Command();
        command.setDeviceId(1);
        command.setType(Command.TYPE_GET_DEVICE_STATUS);
        assertEquals("STATUS\r\n", encoder.encodeCommand(command));

        command = new Command();
        command.setDeviceId(1);
        command.setType(Command.TYPE_CUSTOM);
        command.set(Command.KEY_DATA, "INPUT 0");
        assertEquals("INPUT 0\r\n", encoder.encodeCommand(command));
    }

}
