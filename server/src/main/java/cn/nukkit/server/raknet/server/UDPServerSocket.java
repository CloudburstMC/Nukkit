package cn.nukkit.server.raknet.server;

import cn.nukkit.server.utils.ThreadedLogger;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollDatagramChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class UDPServerSocket extends ChannelInboundHandlerAdapter {

    protected final ThreadedLogger logger;
    protected Bootstrap bootstrap;
    protected EventLoopGroup group;
    protected Channel channel;

    protected ConcurrentLinkedQueue<DatagramPacket> packets = new ConcurrentLinkedQueue<>();

    public UDPServerSocket(ThreadedLogger logger) {
        this(logger, 19132, "0.0.0.0");
    }

    public UDPServerSocket(ThreadedLogger logger, int port) {
        this(logger, port, "0.0.0.0");
    }

    public UDPServerSocket(ThreadedLogger logger, int port, String interfaz) {
        this.logger = logger;
        try {
            if (Epoll.isAvailable()) {
                bootstrap = new Bootstrap()
                        .channel(EpollDatagramChannel.class)
                        .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                        .handler(this)
                        .group(new EpollEventLoopGroup());
                this.logger.info("Epoll is available. EpollEventLoop will be used.");
            } else {
                bootstrap = new Bootstrap()
                        .group(new NioEventLoopGroup())
                        .channel(NioDatagramChannel.class)
                        .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                        .handler(this);
                this.logger.info("Epoll is unavailable. Reverting to NioEventLoop.");
            }
            channel = bootstrap.bind(interfaz, port).sync().channel();
        } catch (Exception e) {
            this.logger.critical("**** FAILED TO BIND TO " + interfaz + ":" + port + "!");
            this.logger.critical("Perhaps a NukkitServer is already running on that port?");
            System.exit(1);
        }
    }

    public void close() {
        this.group.shutdownGracefully();
        try {
            this.channel.closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearPacketQueue() {
        this.packets.clear();
    }

    public DatagramPacket readPacket() throws IOException {
        return this.packets.poll();
    }

    public int writePacket(byte[] data, String dest, int port) throws IOException {
        return this.writePacket(data, new InetSocketAddress(dest, port));
    }

    public int writePacket(byte[] data, InetSocketAddress dest) throws IOException {
        channel.writeAndFlush(new DatagramPacket(Unpooled.wrappedBuffer(data), dest));
        return data.length;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        this.packets.add((DatagramPacket) msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        this.logger.warning(cause.getMessage(), cause);
    }
}
