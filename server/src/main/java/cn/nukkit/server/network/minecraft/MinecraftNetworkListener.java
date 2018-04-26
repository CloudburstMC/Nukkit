package cn.nukkit.server.network.minecraft;

import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.network.NetworkListener;
import cn.nukkit.server.network.raknet.codec.DatagramRakNetDatagramCodec;
import cn.nukkit.server.network.raknet.codec.DatagramRakNetPacketCodec;
import cn.nukkit.server.network.raknet.handler.ExceptionHandler;
import cn.nukkit.server.network.raknet.handler.RakNetDatagramHandler;
import cn.nukkit.server.network.raknet.handler.RakNetPacketHandler;
import cn.nukkit.server.util.NativeUtil;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.unix.UnixChannelOption;
import lombok.extern.log4j.Log4j2;

import java.net.InetSocketAddress;
import java.util.concurrent.ThreadFactory;

@Log4j2
public class MinecraftNetworkListener extends ChannelInitializer<DatagramChannel> implements NetworkListener {
    private final NukkitServer server;
    private final InetSocketAddress address;
    private final Bootstrap bootstrap;
    private DatagramChannel channel;

    public MinecraftNetworkListener(NukkitServer server, String address, int port) {
        this.server = server;
        this.address = new InetSocketAddress(address, port);
        ThreadFactory listenerThreadFactory = new ThreadFactoryBuilder().setDaemon(true).setNameFormat("Minecraft Listener - #%d").build();
        if (Epoll.isAvailable()) {
            log.info("Epoll is available. EpollEventLoop will be used.");
            bootstrap = new Bootstrap()
                    .channel(EpollDatagramChannel.class)
                    .group(new EpollEventLoopGroup(0, listenerThreadFactory))
                    .option(EpollChannelOption.EPOLL_MODE, EpollMode.EDGE_TRIGGERED);
        } else {
            bootstrap = new Bootstrap()
                    .channel(NioDatagramChannel.class)
                    .group(new NioEventLoopGroup(0, listenerThreadFactory));
            log.info("Epoll is unavailable. Reverting to NioEventLoop.");
        }
        if (NativeUtil.isReusePortAvailable()) {
            log.debug("SO_REUSEPORT compatible kernel detected. Binding with multiple listeners.");
            bootstrap.option(UnixChannelOption.SO_REUSEPORT, true);
        }
        bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT).handler(this);
    }


    @Override
    public boolean bind() {
        boolean success = false;
        int netThreads = server.getConfiguration().getAdvanced().getNetworkThreads();
        int threadCount;
        if (NativeUtil.isReusePortAvailable()) {
            threadCount = netThreads < 0 ? Runtime.getRuntime().availableProcessors() : netThreads;
        } else {
            threadCount = 1;
        }

        for (int i = 0; i < threadCount; i++) {
            try {
                ChannelFuture future = bootstrap.bind(address).await();
                if (future.isSuccess()) {
                    log.debug("Bound listener #" + i + " for " + address);
                    success = true;
                } else {
                    log.error("Unable to bind listener #" + i + " for " + address, future.cause());
                    // Continue - as long as we have at least one listener open, we're okay.
                }
            } catch (InterruptedException e) {
                log.info("Binding was interrupted");
            }
        }
        return success;
    }

    @Override
    public void close() {
        bootstrap.group().shutdownGracefully();
        if (channel != null) {
            channel.close().syncUninterruptibly();
        }
    }

    @Override
    protected void initChannel(DatagramChannel channel) throws Exception {
        this.channel = channel;
        /*if (server.getConfiguration().getNetwork().isQueryEnabled()) {
            channel.pipeline()
                    .addLast("queryPacketHandler", new QueryPacketCodec())
                    .addLast("queryDirectPacketHandler", new QueryPacketHandler(server));
        }*/

        channel.pipeline()
                .addLast("datagramRakNetPacketCodec", new DatagramRakNetPacketCodec())
                .addLast("raknetPacketHandler", new RakNetPacketHandler(server))
                .addLast("datagramRakNetDatagramCodec", new DatagramRakNetDatagramCodec(server))
                .addLast("raknetDatagramHandler", new RakNetDatagramHandler(server))
                .addLast("exceptionHandler", new ExceptionHandler());
    }

    @Override
    public InetSocketAddress getAddress() {
        return address;
    }
}
