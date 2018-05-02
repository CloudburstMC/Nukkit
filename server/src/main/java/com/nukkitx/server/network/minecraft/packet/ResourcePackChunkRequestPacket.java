package com.nukkitx.server.network.minecraft.packet;

import com.nukkitx.server.network.minecraft.MinecraftPacket;
import com.nukkitx.server.network.minecraft.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.util.UUID;

import static com.nukkitx.server.network.minecraft.MinecraftUtil.readString;

@Data
public class ResourcePackChunkRequestPacket implements MinecraftPacket {
    private UUID packId;
    private int chunkIndex;

    @Override
    public void encode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void decode(ByteBuf buffer) {
        packId = UUID.fromString(readString(buffer));
        chunkIndex = buffer.readIntLE();
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        handler.handle(this);
    }
}
