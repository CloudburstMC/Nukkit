package cn.nukkit.server.network.raknet.packet;

import cn.nukkit.server.network.raknet.RakNetPacket;
import cn.nukkit.server.network.raknet.RakNetUtil;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class UnconnectedPongPacket implements RakNetPacket {
    private long pingId;
    private long serverId;
    private String advertise;

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeLong(pingId);
        buffer.writeLong(serverId);
        RakNetUtil.writeUnconnectedMagic(buffer);
        RakNetUtil.writeString(buffer, advertise);
    }

    @Override
    public void decode(ByteBuf buffer) {
        pingId = buffer.readLong();
        serverId = buffer.readLong();
        RakNetUtil.verifyUnconnectedMagic(buffer);
        advertise = RakNetUtil.readString(buffer);
    }
}
