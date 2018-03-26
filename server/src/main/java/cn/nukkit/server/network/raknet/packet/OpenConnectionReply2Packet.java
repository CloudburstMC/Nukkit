package cn.nukkit.server.network.raknet.packet;

import cn.nukkit.server.network.raknet.RakNetPacket;
import cn.nukkit.server.network.raknet.RakNetUtil;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.net.InetSocketAddress;

@Data
public class OpenConnectionReply2Packet implements RakNetPacket {
    private long serverId;
    private InetSocketAddress clientAddress;
    private short mtuSize;
    private boolean serverSecurity;

    @Override
    public void encode(ByteBuf buffer) {
        RakNetUtil.writeUnconnectedMagic(buffer);
        buffer.writeLong(serverId);
        RakNetUtil.writeAddress(buffer, clientAddress);
        buffer.writeShort(mtuSize);
        buffer.writeByte((serverSecurity ? 1 : 0));
    }

    @Override
    public void decode(ByteBuf buffer) {
        RakNetUtil.verifyUnconnectedMagic(buffer);
        serverId = buffer.readLong();
        clientAddress = RakNetUtil.readAddress(buffer);
        mtuSize = buffer.readShort();
        serverSecurity = (buffer.readByte() != 0);
    }
}
