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
package org.traccar;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.traccar.helper.DataConverter;
import org.traccar.model.Device;
import org.traccar.model.Position;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

public abstract class ExtendedObjectDecoder extends ChannelInboundHandlerAdapter {

    private void saveOriginal(Object decodedMessage, Object originalMessage) {
        if (Context.getConfig().getBoolean("database.saveOriginal") && decodedMessage instanceof Position) {
            Position position = (Position) decodedMessage;
            if (originalMessage instanceof ByteBuf) {
                ByteBuf buf = (ByteBuf) originalMessage;
                position.set(Position.KEY_ORIGINAL, ByteBufUtil.hexDump(buf));
            } else if (originalMessage instanceof String) {
                position.set(Position.KEY_ORIGINAL, DataConverter.printHex(
                                ((String) originalMessage).getBytes(StandardCharsets.US_ASCII)));
            }
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NetworkMessage networkMessage = (NetworkMessage) msg;
        Object originalMessage = networkMessage.getMessage();
        try {
//          always and only Position/s ?
            Object decodedMessage = decode(ctx.channel(), networkMessage.getRemoteAddress(), originalMessage);
            onMessageEvent(ctx.channel(), networkMessage.getRemoteAddress(), originalMessage, decodedMessage);
            if (decodedMessage == null) {
                decodedMessage = handleEmptyMessage(ctx.channel(), networkMessage.getRemoteAddress(), originalMessage);
            }
            if (decodedMessage != null) {
                if (decodedMessage instanceof Collection) {
                    for (Object o : (Collection) decodedMessage) {
                        saveOriginal(o, originalMessage);
                        addAttributes(o, ctx); // jeets-dcs
                        ctx.fireChannelRead(o);
                    }
                } else {
                    saveOriginal(decodedMessage, originalMessage);
                    addAttributes(decodedMessage, ctx); // jeets-dcs
                    ctx.fireChannelRead(decodedMessage);
                }
            }
        } finally {
            ReferenceCountUtil.release(originalMessage);
        }
    }

    /**
     * As the jeets-dcs only receives the Position object the Device infos are
     * inaccessible and lost. Therefore the missing information for jeets processing
     * is stored as additional attribute (analog to Camel header).
     * <p>
     * The IMEI is a *number* we can rely on for every hardware device. Therefore we
     * could also use the position.id which has no meaning in the dcs process and
     * could be replaced when a (related) device is instantiated and set by
     * interaction with the database. But this could introduce additional risks.
     * Anyhow Traccar has generalized the uniqueId as a String ...
     * <p>
     * Method can also be used to transfer more Device attributes. On the other hand
     * jeets-dcs does not populate device via database and most attributes will be
     * retrieved in a loader process after dcs.
     * @param context 
     */
    private void addAttributes(Object decodedMessage, ChannelHandlerContext context) {
//      validate and verify: always and only Position/s ?
        if (decodedMessage instanceof Position) {
            Position position = (Position) decodedMessage;
            Device device = Context.getIdentityManager().getById(((Position) decodedMessage).getDeviceId());
//          if (device != null)
            position.set("org.jeets.dcs.device.uniqueid", device.getUniqueId());
            position.set("org.jeets.dcs.device.port",
                    ((InetSocketAddress) context.channel().localAddress()).getPort());
        }
    }

    protected void onMessageEvent(
            Channel channel, SocketAddress remoteAddress, Object originalMessage, Object decodedMessage) {
    }

    protected Object handleEmptyMessage(Channel channel, SocketAddress remoteAddress, Object msg) {
        return null;
    }

    protected abstract Object decode(Channel channel, SocketAddress remoteAddress, Object msg) throws Exception;

}
