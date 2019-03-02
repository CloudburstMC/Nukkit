package com.nukkitx.server.level.chunk;

import com.nukkitx.protocol.bedrock.packet.FullChunkDataPacket;

public interface FullChunkDataPacketCreator {

    FullChunkDataPacket createFullChunkDataPacket();
}
