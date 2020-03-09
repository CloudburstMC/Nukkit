package cn.nukkit.network;

import io.netty.buffer.ByteBuf;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public interface AdvancedSourceInterface extends SourceInterface {

    void blockAddress(InetAddress address);

    void blockAddress(InetAddress address, long timeout, TimeUnit unit);

    void unblockAddress(InetAddress address);

    void setNetwork(Network network);

    void sendRawPacket(InetSocketAddress socketAddress, ByteBuf payload);
}
