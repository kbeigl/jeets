package org.jeets.dcs.steps;

import java.util.Date;
import org.apache.camel.Converter;
import org.apache.camel.Exchange;
import org.jeets.model.traccar.jpa.Device;
import org.jeets.protobuf.Jeets;
import org.jeets.protocol.util.Transformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Converter
public final class DeviceProtoToEntityTransformer {

    private static final Logger LOG = LoggerFactory.getLogger(DeviceProtoToEntityTransformer.class);

    private DeviceProtoToEntityTransformer() {}

    @Converter
    public static Device toDevice(Jeets.Device deviceProto, Exchange exchange) throws Exception {
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
