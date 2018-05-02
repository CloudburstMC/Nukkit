package com.nukkitx.server.network.minecraft.packet;

import com.nukkitx.server.network.minecraft.MinecraftPacket;
import com.nukkitx.server.network.minecraft.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class AvailableCommandsPacket implements MinecraftPacket {
    @Override
    public void encode(ByteBuf buffer) {
        //TODO: Rewrite the way this works to cache the contents of this packet instead of processing it every time.
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
