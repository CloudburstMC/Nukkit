package cn.nukkit.network;

import cn.nukkit.Player;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.types.PacketCompressionAlgorithm;
import cn.nukkit.network.session.NetworkPlayerSession;

import java.net.InetSocketAddress;


/**
 * author: MagicDroidX
 * Nukkit Project
 */
public interface SourceInterface {

    @Deprecated
    Integer putPacket(Player player, DataPacket packet);

    @Deprecated
    Integer putPacket(Player player, DataPacket packet, boolean needACK);

    @Deprecated
    Integer putPacket(Player player, DataPacket packet, boolean needACK, boolean immediate);

    NetworkPlayerSession getSession(InetSocketAddress address);

    int getNetworkLatency(Player player);

    void close(Player player);

    void close(Player player, String reason);

    void setName(String name);

    boolean process();

    void shutdown();

    void emergencyShutdown();
}
