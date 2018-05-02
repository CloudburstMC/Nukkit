package com.nukkitx.server.network.raknet.packet;

import com.nukkitx.server.network.raknet.RakNetPacket;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class ConnectedPingPacket implements RakNetPacket {
    private long pingTime;

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeLong(pingTime);
    }

    @Override
    public void decode(ByteBuf buffer) {
        pingTime = buffer.readLong();
    }
}
