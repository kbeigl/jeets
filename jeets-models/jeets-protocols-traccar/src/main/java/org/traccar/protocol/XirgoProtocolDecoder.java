/*
 * Copyright 2015 - 2018 Anton Tananaev (anton@traccar.org)
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

import io.netty.channel.Channel;
import org.traccar.BaseProtocolDecoder;
import org.traccar.DeviceSession;
import org.traccar.Protocol;
import org.traccar.helper.Parser;
import org.traccar.helper.PatternBuilder;
import org.traccar.helper.UnitsConverter;
import org.traccar.model.Position;

import java.net.SocketAddress;
import java.util.regex.Pattern;

public class XirgoProtocolDecoder extends BaseProtocolDecoder {

    public XirgoProtocolDecoder(Protocol protocol) {
        super(protocol);
    }

    private Boolean newFormat;

    private static final Pattern PATTERN_OLD = new PatternBuilder()
            .text("$$")
            .number("(d+),")                     // imei
            .number("(d+),")                     // event
            .number("(dddd)/(dd)/(dd),")         // date (yyyy/mm/dd)
            .number("(dd):(dd):(dd),")           // time (hh:mm:ss)
            .number("(-?d+.?d*),")               // latitude
            .number("(-?d+.?d*),")               // longitude
            .number("(-?d+.?d*),")               // altitude
            .number("(d+.?d*),")                 // speed
            .number("(d+.?d*),")                 // course
            .number("(d+),")                     // satellites
            .number("(d+.?d*),")                 // hdop
            .number("(d+.d+),")                  // battery
            .number("(d+),")                     // gsm
            .number("(d+.?d*),")                 // odometer
            .number("(d+),")                     // gps
            .any()
            .compile();

    private static final Pattern PATTERN_NEW = new PatternBuilder()
            .text("$$")
            .number("(d+),")                     // imei
            .number("(d+),")                     // event
            .number("(dddd)/(dd)/(dd),")         // date (yyyy/mm/dd)
            .number("(dd):(dd):(dd),")           // time (hh:mm:ss)
            .number("(-?d+.?d*),")               // latitude
            .number("(-?d+.?d*),")               // longitude
            .number("(-?d+.?d*),")               // altitude
            .number("(d+.?d*),")                 // speed
            .number("d+.?d*,")                   // acceleration
            .number("d+.?d*,")                   // deceleration
            .number("d+,")
            .number("(d+.?d*),")                 // course
            .number("(d+),")                     // satellites
            .number("(d+.?d*),")                 // hdop
            .number("(d+.?d*),")                 // odometer
            .number("(d+.?d*),")                 // fuel consumption
            .number("(d+.d+),")                  // battery
            .number("(d+),")                     // gsm
            .number("(d+),")                     // gps
            .groupBegin()
            .number("d,")                        // reset mode
            .expression("([01])")                // input 1
            .expression("([01])")                // input 1
            .expression("([01])")                // input 1
            .expression("([01]),")               // output 1
            .number("(d+.?d*),")                 // adc 1
            .number("(d+.?d*),")                 // fuel level
            .number("d+,")                       // engine load
            .number("(d+),")                     // engine hours
            .number("(d+),")                     // oil pressure
            .number("(d+),")                     // oil level
            .number("(-?d+),")                   // oil temperature
            .number("(d+),")                     // coolant pressure
            .number("(d+),")                     // coolant level
            .number("(-?d+)")                    // coolant temperature
            .groupEnd("?")
            .any()
            .compile();

    private void decodeEvent(Position position, int event) {

        position.set(Position.KEY_EVENT, event);

        switch (event) {
            case 4001:
            case 4003:
            case 6011:
            case 6013:
                position.set(Position.KEY_IGNITION, true);
                break;
            case 4002:
            case 4004:
            case 6012:
            case 6014:
                position.set(Position.KEY_IGNITION, false);
                break;
            case 4005:
                position.set(Position.KEY_CHARGE, false);
                break;
            case 6002:
                position.set(Position.KEY_ALARM, Position.ALARM_OVERSPEED);
                break;
            case 6006:
                position.set(Position.KEY_ALARM, Position.ALARM_ACCELERATION);
                break;
            case 6007:
                position.set(Position.KEY_ALARM, Position.ALARM_BRAKING);
                break;
            case 6008:
                position.set(Position.KEY_ALARM, Position.ALARM_LOW_POWER);
                break;
            case 6009:
                position.set(Position.KEY_ALARM, Position.ALARM_POWER_CUT);
                break;
            case 6010:
                position.set(Position.KEY_ALARM, Position.ALARM_POWER_RESTORED);
                break;
            case 6016:
                position.set(Position.KEY_ALARM, Position.ALARM_IDLE);
                break;
            case 6017:
                position.set(Position.KEY_ALARM, Position.ALARM_TOW);
                break;
            case 6030:
            case 6071:
                position.set(Position.KEY_MOTION, true);
                break;
            case 6031:
                position.set(Position.KEY_MOTION, false);
                break;
            case 6032:
                position.set(Position.KEY_ALARM, Position.ALARM_PARKING);
                break;
            case 6090:
                position.set(Position.KEY_ALARM, Position.ALARM_REMOVING);
                break;
            case 6091:
                position.set(Position.KEY_ALARM, Position.ALARM_LOW_BATTERY);
                break;
            default:
                break;
        }
    }

    @Override
    protected Object decode(
            Channel channel, SocketAddress remoteAddress, Object msg) throws Exception {

        String sentence = (String) msg;

        Parser parser;
        if (newFormat == null) {
            parser = new Parser(PATTERN_NEW, sentence);
            if (parser.matches()) {
                newFormat = true;
            } else {
                parser = new Parser(PATTERN_OLD, sentence);
                if (parser.matches()) {
                    newFormat = false;
                } else {
                    return null;
                }
            }
        } else {
            if (newFormat) {
                parser = new Parser(PATTERN_NEW, sentence);
            } else {
                parser = new Parser(PATTERN_OLD, sentence);
            }
            if (!parser.matches()) {
                return null;
            }
        }

        Position position = new Position(getProtocolName());

        DeviceSession deviceSession = getDeviceSession(channel, remoteAddress, parser.next());
        if (deviceSession == null) {
            return null;
        }
        position.setDeviceId(deviceSession.getDeviceId());

        decodeEvent(position, parser.nextInt());

        position.setTime(parser.nextDateTime());

        position.setLatitude(parser.nextDouble(0));
        position.setLongitude(parser.nextDouble(0));
        position.setAltitude(parser.nextDouble(0));
        position.setSpeed(UnitsConverter.knotsFromMph(parser.nextDouble(0)));
        position.setCourse(parser.nextDouble(0));

        position.set(Position.KEY_SATELLITES, parser.nextInt());
        position.set(Position.KEY_HDOP, parser.nextDouble());

        if (newFormat) {
            position.set(Position.KEY_ODOMETER, UnitsConverter.metersFromMiles(parser.nextDouble(0)));
            position.set(Position.KEY_FUEL_CONSUMPTION, parser.next());
        }

        position.set(Position.KEY_BATTERY, parser.nextDouble(0));
        position.set(Position.KEY_RSSI, parser.nextDouble());

        if (!newFormat) {
            position.set(Position.KEY_ODOMETER, UnitsConverter.metersFromMiles(parser.nextDouble(0)));
        }

        position.setValid(parser.nextInt(0) == 1);

        if (newFormat && parser.hasNext(13)) {
            position.set(Position.PREFIX_IN + 1, parser.nextInt());
            position.set(Position.PREFIX_IN + 2, parser.nextInt());
            position.set(Position.PREFIX_IN + 3, parser.nextInt());
            position.set(Position.PREFIX_OUT + 1, parser.nextInt());
            position.set(Position.PREFIX_ADC + 1, parser.nextDouble());
            position.set(Position.KEY_FUEL_LEVEL, parser.nextDouble());
            position.set(Position.KEY_HOURS, UnitsConverter.msFromHours(parser.nextInt()));
            position.set("oilPressure", parser.nextInt());
            position.set("oilLevel", parser.nextInt());
            position.set("oilTemp", parser.nextInt());
            position.set("coolantPressure", parser.nextInt());
            position.set("coolantLevel", parser.nextInt());
            position.set("coolantTemp", parser.nextInt());
        }

        return position;
    }

}
