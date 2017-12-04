package cn.nukkit.server.network;

import cn.nukkit.server.Player;
import cn.nukkit.server.network.protocol.DataPacket;


/**
 * author: MagicDroidX
 * Nukkit Project
 */
public interface SourceInterface {

    Integer putPacket(Player player, DataPacket packet);

    Integer putPacket(Player player, DataPacket packet, boolean needACK);

    Integer putPacket(Player player, DataPacket packet, boolean needACK, boolean immediate);

    int getNetworkLatency(Player player);

    void close(Player player);

    void close(Player player, String reason);

    void setName(String name);

    boolean process();

    void shutdown();

    void emergencyShutdown();
}
