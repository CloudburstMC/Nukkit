package com.nukkitx.server.network.raknet.packet;

import com.nukkitx.server.network.raknet.RakNetPacket;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class DisconnectNotificationPacket implements RakNetPacket {

    @Override
    public void encode(ByteBuf buffer) {
        // No payload
    }

    @Override
    public void decode(ByteBuf buffer) {
        // No payload
    }
}
