package cn.nukkit.network;

import io.netty.buffer.ByteBuf;

import java.net.InetSocketAddress;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
public interface AdvancedSourceInterface extends SourceInterface {

    @Deprecated
    void blockAddress(InetSocketAddress address);

    @Deprecated
    void blockAddress(InetSocketAddress address, int timeout);

    @Deprecated
    void unblockAddress(InetSocketAddress address);

    @Deprecated
    void setNetwork(Network network);

    @Deprecated
    void sendRawPacket(InetSocketAddress socketAddress, ByteBuf payload);
}
