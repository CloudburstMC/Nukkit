package com.nukkitx.server.level.chunk;

import com.nukkitx.server.network.minecraft.packet.WrappedPacket;

public interface FullChunkDataPacketCreator {

    WrappedPacket createFullChunkDataPacket();
}
