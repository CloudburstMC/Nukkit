package com.nukkitx.server.level.chunk;

import com.nukkitx.server.network.bedrock.packet.WrappedPacket;

public interface FullChunkDataPacketCreator {

    WrappedPacket createFullChunkDataPacket();
}
