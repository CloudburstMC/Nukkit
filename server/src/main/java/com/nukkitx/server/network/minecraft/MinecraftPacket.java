package com.nukkitx.server.network.minecraft;

import com.nukkitx.server.network.raknet.NetworkPacket;

public interface MinecraftPacket extends NetworkPacket {
    void handle(NetworkPacketHandler handler);
}
