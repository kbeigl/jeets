/*
 * Copyright 2012 - 2018 Anton Tananaev (anton@traccar.org)
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

import io.netty.bootstrap.AbstractBootstrap;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.net.InetSocketAddress;

public abstract class TrackerServer {

    private final boolean datagram;
    @SuppressWarnings("rawtypes")
    private final AbstractBootstrap bootstrap;

    public boolean isDatagram() {
        return datagram;
    }

//  seams like constructor is invoked twice > investigate
    public TrackerServer(boolean datagram, String protocol) {
        this.datagram = datagram;

//      setProtocolClass here ?
//      try {
        address = Context.getConfig().getString(protocol + ".address");
        port = Context.getConfig().getInteger(protocol + ".port");
//      } catch (NullPointerException e) {  // JeeTS
//          port = 5200;
//      }

        pipelineFactory = new BasePipelineFactory(this, protocol) {
            @Override
            protected void addProtocolHandlers(PipelineBuilder pipeline) {
                TrackerServer.this.addProtocolHandlers(pipeline);
            }
        };

//      handle this in Camel uri (spring.xml). Deactivate here?
        if (datagram) {

            this.bootstrap = new Bootstrap()
                    .group(EventLoopGroupFactory.getWorkerGroup())
                    .channel(NioDatagramChannel.class)
                    .handler(pipelineFactory);

        } else {

            this.bootstrap = new ServerBootstrap()
                    .group(EventLoopGroupFactory.getBossGroup(), EventLoopGroupFactory.getWorkerGroup())
                    .channel(NioServerSocketChannel.class)
                    .childHandler(pipelineFactory);
        }
    }

    private BasePipelineFactory pipelineFactory = null;
    public BasePipelineFactory getPipelineFactory() {
        return pipelineFactory;
    }

    /* This is a work around to conserve the protocol class in order to
     * createPipelineFactory(NettyConsumer consumer) when being invoked from the
     * registered *Protocol instance. There should be a better way ...
     */
    @SuppressWarnings("rawtypes")
    private Class protocolClass;    // extends BaseProtocol
    public void setProtocolClass(@SuppressWarnings("rawtypes") Class protocolClass) {
        this.protocolClass = protocolClass;
    }

    @SuppressWarnings("unchecked")
    public Class<BaseProtocol> getProtocolClass() {
        return protocolClass;
    }

    protected abstract void addProtocolHandlers(PipelineBuilder pipeline);

    private int port;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    private String address;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    private final ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    public ChannelGroup getChannelGroup() {
        return channelGroup;
    }

    public void start() throws Exception {
        InetSocketAddress endpoint;
        if (address == null) {
            endpoint = new InetSocketAddress(port);
        } else {
            endpoint = new InetSocketAddress(address, port);
        }

        Channel channel = bootstrap.bind(endpoint).sync().channel();
        if (channel != null) {
            getChannelGroup().add(channel);
        }
    }

    public void stop() {
        channelGroup.close().awaitUninterruptibly();
    }

}
