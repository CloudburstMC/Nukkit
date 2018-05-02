package com.nukkitx.server.network.minecraft.packet;

import com.nukkitx.server.network.minecraft.MinecraftPacket;
import com.nukkitx.server.network.minecraft.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static com.nukkitx.nbt.util.VarInt.readSignedInt;
import static com.nukkitx.nbt.util.VarInt.writeSignedInt;

@Data
public class RequestChunkRadiusPacket implements MinecraftPacket {
    private int radius;

    @Override
    public void encode(ByteBuf buffer) {
        writeSignedInt(buffer, radius);
    }

    @Override
    public void decode(ByteBuf buffer) {
        radius = readSignedInt(buffer);
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        handler.handle(this);
    }
}
