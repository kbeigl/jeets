package org.traccar.protocol;

import org.junit.Test;
import org.traccar.ProtocolTest;
import org.traccar.model.Command;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Date;

import static org.junit.Assert.assertEquals;

public class H02ProtocolEncoderTest extends ProtocolTest {

    private H02ProtocolEncoder encoder = new H02ProtocolEncoder();
    private Date time = Date.from(
            LocalDateTime.of(LocalDate.now(), LocalTime.of(1, 2, 3)).atZone(ZoneOffset.systemDefault()).toInstant());

    @Test
    public void testAlarmArmEncode() throws Exception {

        Command command = new Command();
        command.setDeviceId(1);
        command.setType(Command.TYPE_ALARM_ARM);

        assertEquals("*HQ,123456789012345,SCF,010203,0,0#", encoder.encodeCommand(command, time));
    }

    @Test
    public void testAlarmDisarmEncode() throws Exception {

        Command command = new Command();
        command.setDeviceId(1);
        command.setType(Command.TYPE_ALARM_DISARM);

        assertEquals("*HQ,123456789012345,SCF,010203,1,1#", encoder.encodeCommand(command, time));
    }

    @Test
    public void testEngineStopEncode() throws Exception {

        Command command = new Command();
        command.setDeviceId(1);
        command.setType(Command.TYPE_ENGINE_STOP);

        assertEquals("*HQ,123456789012345,S20,010203,1,1#", encoder.encodeCommand(command, time));
    }

    @Test
    public void testEngineResumeEncode() throws Exception {

        Command command = new Command();
        command.setDeviceId(1);
        command.setType(Command.TYPE_ENGINE_RESUME);

        assertEquals("*HQ,123456789012345,S20,010203,1,0#", encoder.encodeCommand(command, time));
    }

    @Test
    public void testPositionPeriodicEncode() throws Exception {

        Command command = new Command();
        command.setDeviceId(1);
        command.set(Command.KEY_FREQUENCY, 10);
        command.setType(Command.TYPE_POSITION_PERIODIC);

        assertEquals("*HQ,123456789012345,S71,010203,22,10#", encoder.encodeCommand(command, time));
    }

}
