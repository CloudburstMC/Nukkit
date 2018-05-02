package com.nukkitx.server.network.minecraft.packet;

import com.nukkitx.server.network.minecraft.MinecraftPacket;
import com.nukkitx.server.network.minecraft.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.util.UUID;

import static com.nukkitx.nbt.util.VarInt.writeUnsignedInt;
import static com.nukkitx.server.network.minecraft.MinecraftUtil.writeString;

@Data
public class ResourcePackDataInfoPacket implements MinecraftPacket {
    private UUID packId;
    private int maxChunkSize;
    private int chunkCount;
    private int compressedPackSize;
    private byte[] hash;

    @Override
    public void encode(ByteBuf buffer) {
        writeString(buffer, packId.toString());
        buffer.writeIntLE(maxChunkSize);
        buffer.writeIntLE(chunkCount);
        buffer.writeLongLE(compressedPackSize);
        writeUnsignedInt(buffer, hash.length);
        buffer.writeBytes(hash);
    }

    @Override
    public void decode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        // Only clientbound
    }
}
