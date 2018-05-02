package com.nukkitx.server.network.minecraft.packet;

import com.nukkitx.server.network.minecraft.MinecraftPacket;
import com.nukkitx.server.network.minecraft.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static com.nukkitx.server.network.minecraft.MinecraftUtil.readUniqueEntityId;
import static com.nukkitx.server.network.minecraft.MinecraftUtil.writeUniqueEntityId;

@Data
public class MapInfoRequestPacket implements MinecraftPacket {
    private long mapId;

    @Override
    public void encode(ByteBuf buffer) {
        writeUniqueEntityId(buffer, mapId);
    }

    @Override
    public void decode(ByteBuf buffer) {
        mapId = readUniqueEntityId(buffer);
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        handler.handle(this);
    }
}
