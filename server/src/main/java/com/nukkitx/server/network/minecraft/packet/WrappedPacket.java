package com.nukkitx.server.network.minecraft.packet;

import com.nukkitx.server.network.minecraft.MinecraftPacket;
import com.nukkitx.server.network.raknet.RakNetPacket;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class WrappedPacket implements RakNetPacket {
    private final List<MinecraftPacket> packets = new ArrayList<>();
    private ByteBuf payload;

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeBytes(payload);
    }

    @Override
    public void decode(ByteBuf buffer) {
        payload = buffer.readBytes(buffer.readableBytes());
    }
}
