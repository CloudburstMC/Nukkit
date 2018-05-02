package com.nukkitx.server.network.raknet.packet;

import com.nukkitx.server.network.raknet.RakNetPacket;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class ConnectionRequestPacket implements RakNetPacket {
    private long clientGuid;
    private long timestamp;
    private boolean serverSecurity;

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeLong(clientGuid);
        buffer.writeLong(timestamp);
        buffer.writeBoolean(serverSecurity);
    }

    @Override
    public void decode(ByteBuf buffer) {
        clientGuid = buffer.readLong();
        timestamp = buffer.readLong();
        serverSecurity = buffer.readBoolean();
    }
}
