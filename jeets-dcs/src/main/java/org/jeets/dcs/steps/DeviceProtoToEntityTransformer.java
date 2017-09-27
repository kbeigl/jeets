package org.jeets.dcs.steps;
/**
 * Copyright 2017 The Java EE Tracking System - JeeTS
 * Copyright 2017 Kristof Beiglböck kbeigl@jeets.org
 *
 * The JeeTS Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

import java.util.Date;
import org.apache.camel.Converter;
import org.apache.camel.Exchange;
import org.jeets.model.traccar.jpa.Device;
import org.jeets.protocol.Traccar;
import org.jeets.protocol.util.Transformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Message Transformer of a Device Proto Message to a Device @Entity Bean
 */
@Converter
public final class DeviceProtoToEntityTransformer {

    private static final Logger LOG = LoggerFactory.getLogger(DeviceProtoToEntityTransformer.class);

    private DeviceProtoToEntityTransformer() {
    }

    /**
     * Transform Traccar.Device protobuffer message to Device Entity.
     */
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
