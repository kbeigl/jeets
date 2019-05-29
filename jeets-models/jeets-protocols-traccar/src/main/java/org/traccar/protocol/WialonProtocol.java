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

import org.traccar.BaseProtocol;
import org.traccar.Context;
import org.traccar.PipelineBuilder;
import org.traccar.TrackerServer;
import org.traccar.model.Command;

import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.nio.charset.StandardCharsets;
public class WialonProtocol extends BaseProtocol {

    public WialonProtocol() {
        setSupportedDataCommands(
                Command.TYPE_REBOOT_DEVICE,
                Command.TYPE_SEND_USSD,
                Command.TYPE_IDENTIFICATION,
                Command.TYPE_OUTPUT_CONTROL);
        addServer(new TrackerServer(false, getName()) {
            @Override
            protected void addProtocolHandlers(PipelineBuilder pipeline) {
                pipeline.addLast(new LineBasedFrameDecoder(4 * 1024));
                pipeline.addLast(new StringEncoder());
                boolean utf8 = Context.getConfig().getBoolean(getName() + ".utf8");
                if (utf8) {
                    pipeline.addLast(new StringDecoder(StandardCharsets.UTF_8));
                } else {
                    pipeline.addLast(new StringDecoder());
                }
                pipeline.addLast(new WialonProtocolEncoder());
                pipeline.addLast(new WialonProtocolDecoder(WialonProtocol.this));
            }
        });
    }

}
