package cn.nukkit.network;

import cn.nukkit.Player;
import cn.nukkit.network.session.NetworkPlayerSession;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;

import java.net.InetSocketAddress;


/**
 * author: MagicDroidX
 * Nukkit Project
 */
public interface SourceInterface {

    @Deprecated
    default Integer putPacket(Player player, BedrockPacket packet) {
        throw new UnsupportedOperationException("This method is deprecated");
    }

    @Deprecated
    default Integer putPacket(Player player, BedrockPacket packet, boolean needACK) {
        throw new UnsupportedOperationException("This method is deprecated");
    }

    @Deprecated
    default Integer putPacket(Player player, BedrockPacket packet, boolean needACK, boolean immediate) {
        throw new UnsupportedOperationException("This method is deprecated");
    }

    NetworkPlayerSession getSession(InetSocketAddress address);

    int getNetworkLatency(Player player);

    void close(Player player);

    void close(Player player, String reason);

    void setName(String name);

    boolean process();

    void shutdown();

    void emergencyShutdown();
}
