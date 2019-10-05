package cn.nukkit.network;

import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.player.Player;


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
