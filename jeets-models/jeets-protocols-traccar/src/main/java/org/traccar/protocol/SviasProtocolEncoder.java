/*
 * Copyright 2018 Anton Tananaev (anton@traccar.org)
 * Copyright 2018 Andrey Kunitsyn (andrey@traccar.org)
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
package org.traccar.protocol;

import org.traccar.StringProtocolEncoder;
import org.traccar.model.Command;

public class SviasProtocolEncoder extends StringProtocolEncoder {

    @Override
    protected Object encodeCommand(Command command) {
        switch (command.getType()) {
            case Command.TYPE_CUSTOM:
                return formatCommand(command, "{%s}", Command.KEY_DATA);
            case Command.TYPE_POSITION_SINGLE:
                return formatCommand(command, "AT+STR=1*");
            case Command.TYPE_SET_ODOMETER:
                return formatCommand(command, "AT+ODT={%s}*", Command.KEY_DATA);
            case Command.TYPE_ENGINE_STOP:
                return formatCommand(command, "AT+OUT=1,1*");
            case Command.TYPE_ENGINE_RESUME:
                return formatCommand(command, "AT+OUT=1,0*");
            case Command.TYPE_ALARM_ARM:
                return formatCommand(command, "AT+OUT=2,1*");
            case Command.TYPE_ALARM_DISARM:
                return formatCommand(command, "AT+OUT=2,0*");
            case Command.TYPE_ALARM_REMOVE:
                return formatCommand(command, "AT+PNC=600*");
            default:
                return null;
        }
    }

}
