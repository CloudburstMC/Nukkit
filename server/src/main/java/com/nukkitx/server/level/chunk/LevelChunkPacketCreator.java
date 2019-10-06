package com.nukkitx.server.level.chunk;

import com.nukkitx.protocol.bedrock.packet.LevelChunkPacket;

public interface LevelChunkPacketCreator {

    LevelChunkPacket createLevelChunkPacket();
}
