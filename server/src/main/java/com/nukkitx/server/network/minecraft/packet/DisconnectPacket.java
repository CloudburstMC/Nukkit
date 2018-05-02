package com.nukkitx.server.network.minecraft.packet;

import com.nukkitx.server.network.minecraft.MinecraftPacket;
import com.nukkitx.server.network.minecraft.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static com.nukkitx.server.network.minecraft.MinecraftUtil.writeString;

@Data
public class DisconnectPacket implements MinecraftPacket {
    private boolean disconnectScreenHidden;
    private String kickMessage;

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeBoolean(disconnectScreenHidden);
        if (!disconnectScreenHidden) {
            writeString(buffer, kickMessage);
        }
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
