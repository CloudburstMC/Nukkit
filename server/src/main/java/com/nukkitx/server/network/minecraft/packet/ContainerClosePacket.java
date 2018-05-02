package com.nukkitx.server.network.minecraft.packet;

import com.nukkitx.server.network.minecraft.MinecraftPacket;
import com.nukkitx.server.network.minecraft.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class ContainerClosePacket implements MinecraftPacket {
    private byte windowId;

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeByte(windowId);
    }

    @Override
    public void decode(ByteBuf buffer) {
        windowId = buffer.readByte();
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        handler.handle(this);
    }
}
