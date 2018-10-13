package com.nukkitx.server.network.bedrock;

import com.nukkitx.network.NetworkPacket;
import org.apache.logging.log4j.LogManager;

public interface BedrockPacket extends NetworkPacket {

    default void handle(BedrockPacketHandler handler) {
        Class<?> clazz = getClass();
        LogManager.getLogger(clazz).debug("Received client-bound only " + clazz.getSimpleName());
    }
}
