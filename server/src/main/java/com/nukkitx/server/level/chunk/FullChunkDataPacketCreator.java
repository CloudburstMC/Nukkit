package com.nukkitx.server.level.chunk;

import com.nukkitx.server.network.bedrock.packet.FullChunkDataPacket;

public interface FullChunkDataPacketCreator {

    FullChunkDataPacket createFullChunkDataPacket();
}
