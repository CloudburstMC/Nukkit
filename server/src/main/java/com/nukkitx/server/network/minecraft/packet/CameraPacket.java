package com.nukkitx.server.network.minecraft.packet;

import com.nukkitx.server.network.minecraft.MinecraftPacket;
import com.nukkitx.server.network.minecraft.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static com.nukkitx.server.network.minecraft.MinecraftUtil.writeUniqueEntityId;

@Data
public class CameraPacket implements MinecraftPacket {
    private long cameraUniqueEntityId;
    private long playerUniqueEntityId;

    @Override
    public void encode(ByteBuf buffer) {
        writeUniqueEntityId(buffer, cameraUniqueEntityId);
        writeUniqueEntityId(buffer, playerUniqueEntityId);
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
