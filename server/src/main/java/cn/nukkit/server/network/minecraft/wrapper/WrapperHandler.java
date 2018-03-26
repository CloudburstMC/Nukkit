package cn.nukkit.server.network.minecraft.wrapper;

import cn.nukkit.server.network.minecraft.MinecraftPacket;
import io.netty.buffer.ByteBuf;

import java.util.Collection;
import java.util.List;

public interface WrapperHandler {
    default ByteBuf compressPackets(Collection<MinecraftPacket> packets) {
        return compressPackets(packets.toArray(new MinecraftPacket[0]));
    }

    ByteBuf compressPackets(MinecraftPacket... packets);

    List<MinecraftPacket> decompressPackets(ByteBuf compressed);
}