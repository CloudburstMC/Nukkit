package com.nukkitx.server.network.raknet.packet;

import com.nukkitx.server.network.raknet.RakNetPacket;
import com.nukkitx.server.network.raknet.RakNetUtil;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.net.InetSocketAddress;

@Data
public class OpenConnectionRequest2Packet implements RakNetPacket {
    private InetSocketAddress serverAddress;
    private short mtuSize;
    private long clientId;

    @Override
    public void encode(ByteBuf buffer) {
        RakNetUtil.writeUnconnectedMagic(buffer);
        RakNetUtil.writeAddress(buffer, serverAddress);
        buffer.writeShort(mtuSize);
        buffer.writeLong(clientId);
    }

    @Override
    public void decode(ByteBuf buffer) {
        RakNetUtil.verifyUnconnectedMagic(buffer);
        serverAddress = RakNetUtil.readAddress(buffer);
        mtuSize = buffer.readShort();
        clientId = buffer.readLong();
    }
}
