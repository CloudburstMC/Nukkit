package com.nukkitx.server.network.minecraft.packet;

import com.nukkitx.server.network.minecraft.MinecraftPacket;
import com.nukkitx.server.network.minecraft.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.util.UUID;

import static com.nukkitx.server.network.minecraft.MinecraftUtil.writeString;

@Data
public class ResourcePackChunkDataPacket implements MinecraftPacket {
    private UUID packId;
    private int chunkIndex;
    private long progress;
    private byte[] data;

    @Override
    public void encode(ByteBuf buffer) {
        writeString(buffer, packId.toString());
        buffer.writeIntLE(chunkIndex);
        buffer.writeLongLE(progress);
        buffer.writeIntLE(data.length);
        buffer.writeBytes(data);
    }

    @Override
    public void decode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        // Only client bound.
    }
}
