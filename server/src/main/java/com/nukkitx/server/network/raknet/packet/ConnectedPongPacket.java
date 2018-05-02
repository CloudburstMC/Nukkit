package com.nukkitx.server.network.raknet.packet;

import com.nukkitx.server.network.raknet.RakNetPacket;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class ConnectedPongPacket implements RakNetPacket {
    private long pingTime;
    private long pongTime;

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeLong(pingTime);
        buffer.writeLong(pongTime);
    }

    @Override
    public void decode(ByteBuf buffer) {
        pingTime = buffer.readLong();
        pongTime = buffer.readLong();
    }
}
