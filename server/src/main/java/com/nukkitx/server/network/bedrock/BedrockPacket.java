package com.nukkitx.server.network.bedrock;

import com.nukkitx.network.NetworkPacket;

public interface BedrockPacket extends NetworkPacket {

    default void handle(NetworkPacketHandler handler) {
    }
}
