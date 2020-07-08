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

import org.apache.camel.component.netty.ServerInitializerFactory;

public abstract class TrackerServer {

    private final boolean datagram;
    private AbstractBootstrap bootstrap = null;

    public boolean isDatagram() {
        return datagram;
    }

    public TrackerServer(boolean datagram, String protocol) {

        this.datagram = datagram;
        if (Context.getConfig() != null) {
//          required for Camel ?
            address = Context.getConfig().getString(protocol + ".address");
            port = Context.getConfig().getInteger(protocol + ".port");
        }

//      one factory instance for each TrackerServer instance
//      set breakpoint and analyze multiple calls
        pipelineFactory = new BasePipelineFactory(this, protocol) {
            @Override
            protected void addProtocolHandlers(PipelineBuilder pipeline) {
                TrackerServer.this.addProtocolHandlers(pipeline);
            }
        };

//      bind in original context OR let Camel take control of the life cycle
        if (Context.legacy) {
            if (datagram) {
                this.bootstrap = new Bootstrap()
                        .group(EventLoopGroupFactory.getWorkerGroup())
                        .channel(NioDatagramChannel.class)
                        .handler(pipelineFactory);
            } else {
                this.bootstrap = new ServerBootstrap()
                        .group(EventLoopGroupFactory.getBossGroup(), 
                                EventLoopGroupFactory.getWorkerGroup())
                        .channel(NioServerSocketChannel.class)
                        .childHandler(pipelineFactory);
            }
        }
    }

    private BasePipelineFactory pipelineFactory = null;

    /**
     * Each protocol has its own factory a returned pipeline should be named after.
     * <p>
     * Every new factory has a dangling reference to the TrackerServer and its
     * BasePipeline!
     * 
     * @return the pipeline for the associated protocol
     */
    public ServerInitializerFactory getServerInitializerFactory() {
        return pipelineFactory.new CamelPipelineFactory(null);
    }

    protected abstract void addProtocolHandlers(PipelineBuilder pipeline);

    private int port;
    public int getPort() {
        return port;
    }

    private String address;
    public String getAddress() {
        return address;
    }

//  observe DEBUG messages when this is created > remove for JeeTS
    private final ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    public ChannelGroup getChannelGroup() {
        return channelGroup;
    }

    /**
     * Only apply start() and stop() methods in original Traccar context.
     */
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
