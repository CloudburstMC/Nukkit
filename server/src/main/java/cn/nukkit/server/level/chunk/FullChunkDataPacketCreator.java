package cn.nukkit.server.level.chunk;

import cn.nukkit.server.network.minecraft.packet.WrappedPacket;

public interface FullChunkDataPacketCreator {

    WrappedPacket createFullChunkDataPacket();
}
