package com.nukkitx.server.network.minecraft.packet;

import com.nukkitx.server.network.minecraft.MinecraftPacket;
import com.nukkitx.server.network.minecraft.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static com.nukkitx.nbt.util.VarInt.writeSignedInt;

@Data
public class ChunkRadiusUpdatePacket implements MinecraftPacket {
    private int radius;

    @Override
    public void encode(ByteBuf buffer) {
        writeSignedInt(buffer, radius);
    }

    @Override
    public void decode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        // This packet isn't handled
    }
}
