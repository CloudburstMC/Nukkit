package cn.nukkit.server.network.minecraft;

import cn.nukkit.server.network.raknet.NetworkPacket;

public interface MinecraftPacket extends NetworkPacket {
    void handle(NetworkPacketHandler handler);
}
