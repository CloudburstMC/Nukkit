package cn.nukkit.server.network.rcon;

import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.network.NetworkListener;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import lombok.Getter;

import java.net.InetSocketAddress;
import java.nio.ByteOrder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class RconNetworkListener extends ChannelInitializer<SocketChannel> implements NetworkListener {
    private final NukkitServer server;
    private final InetSocketAddress address;
    private final ServerBootstrap bootstrap;
    @Getter
    private final ExecutorService commandExecutionService = Executors.newSingleThreadExecutor(
            new ThreadFactoryBuilder().setDaemon(true).setNameFormat("Voxelwind RCON Command Executor").build());
    private final byte[] password;
    private SocketChannel channel;

    public RconNetworkListener(NukkitServer server, byte[] password, String address, int port) {
        this.server = server;
        this.password = password;
        this.address = new InetSocketAddress(address, port);
        this.bootstrap = new ServerBootstrap()
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childHandler(this);

        ThreadFactory factory = new ThreadFactoryBuilder().setDaemon(true).setNameFormat("RCON Listener").build();
        if (Epoll.isAvailable()) {
            this.bootstrap.channel(EpollServerSocketChannel.class)
                    .group(new EpollEventLoopGroup(1, factory));
        } else {
            this.bootstrap.channel(NioServerSocketChannel.class)
                    .group(new NioEventLoopGroup(1, factory));
        }
    }

    @Override
    public boolean bind() {
        return bootstrap.bind(address).awaitUninterruptibly().isSuccess();
    }

    @Override
    public void close() {
        commandExecutionService.shutdown();
        try {
            commandExecutionService.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            // Ignore
        }
        bootstrap.group().shutdownGracefully();
        if (channel != null) {
            channel.close().syncUninterruptibly();
        }
    }

    @Override
    public InetSocketAddress getAddress() {
        return null;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        this.channel = socketChannel;

        channel.pipeline().addLast("lengthDecoder", new LengthFieldBasedFrameDecoder(ByteOrder.LITTLE_ENDIAN, 4096, 0, 4, 0, 4, true));
        channel.pipeline().addLast("rconDecoder", new RconCodec());
        channel.pipeline().addLast("rconHandler", new RconHandler(server, this, password));
        channel.pipeline().addLast("lengthPrepender", new LengthFieldPrepender(ByteOrder.LITTLE_ENDIAN, 4, 0, false));
    }
}
