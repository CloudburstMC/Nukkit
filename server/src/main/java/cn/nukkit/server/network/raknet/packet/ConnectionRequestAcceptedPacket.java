package cn.nukkit.server.network.raknet.packet;

import cn.nukkit.server.network.raknet.RakNetPacket;
import cn.nukkit.server.network.raknet.RakNetUtil;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.net.InetSocketAddress;

@Data
public class ConnectionRequestAcceptedPacket implements RakNetPacket {
    private InetSocketAddress systemAddress;
    private short systemIndex;
    private InetSocketAddress[] systemAddresses;
    private long incomingTimestamp;
    private long systemTimestamp;

    @Override
    public void encode(ByteBuf buffer) {
        RakNetUtil.writeAddress(buffer, systemAddress);
        buffer.writeShort(systemIndex);
        for (InetSocketAddress address : systemAddresses) {
            RakNetUtil.writeAddress(buffer, address);
        }
        buffer.writeLong(incomingTimestamp);
        buffer.writeLong(systemTimestamp);
    }

    @Override
    public void decode(ByteBuf buffer) {
        systemAddress = RakNetUtil.readAddress(buffer);
        systemIndex = buffer.readShort();
        systemAddresses = new InetSocketAddress[20];
        for (int i = 0; i < 10; i++) {
            systemAddresses[i] = RakNetUtil.readAddress(buffer);
        }
        incomingTimestamp = buffer.readLong();
        systemTimestamp = buffer.readLong();
    }
}
