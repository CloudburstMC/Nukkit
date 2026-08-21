package cn.nukkit.network;

import io.netty.buffer.ByteBuf;

import java.net.InetSocketAddress;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
public interface AdvancedSourceInterface extends SourceInterface {

    void blockAddress(InetSocketAddress address);

    void blockAddress(InetSocketAddress address, int timeout);

    void unblockAddress(InetSocketAddress address);

    void setNetwork(Network network);

    void sendRawPacket(InetSocketAddress socketAddress, ByteBuf payload);
}
