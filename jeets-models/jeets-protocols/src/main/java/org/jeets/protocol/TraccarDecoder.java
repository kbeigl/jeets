package org.jeets.protocol;

import java.util.Date;
import org.apache.camel.Converter;
import org.apache.camel.Exchange;
import org.jeets.model.traccar.jpa.Device;
import org.jeets.protobuf.Traccar;
import org.jeets.protocol.TraccarDecoder;
import org.jeets.protocol.util.Transformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DeviceProtoToEntityTransformer
 */
@Converter
public final class TraccarDecoder {

    private static final Logger LOG = LoggerFactory.getLogger(TraccarDecoder.class);

    private TraccarDecoder() {}

    @Converter
    public static Device toDevice(Traccar.Device deviceProto, Exchange exchange) throws Exception {
        LOG.info("Transformer receives device proto: {} at {}", 
                deviceProto.getUniqueid(), new Date().getTime());

        Device deviceEntity = Transformer.protoToEntityDevice(deviceProto);
        LOG.info("Transformer returns device entity {} with {} positions at {}", 
                deviceEntity.getUniqueid(), deviceEntity.getPositions().size(), new Date().getTime());

        return deviceEntity;
//      a single line would do without logging!
//      return Transformer.protoToEntityDevice(deviceProto);
    }

}
