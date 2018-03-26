package cn.nukkit.server.network.raknet.packet;

import cn.nukkit.server.network.raknet.RakNetPacket;
import cn.nukkit.server.network.raknet.RakNetUtil;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class UnconnectedPingPacket implements RakNetPacket {
    private long pingId;
    private long serverId;

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeLong(pingId);
        RakNetUtil.writeUnconnectedMagic(buffer);
        buffer.writeLong(serverId);
    }

    @Override
    public void decode(ByteBuf buffer) {
        pingId = buffer.readLong();
        RakNetUtil.verifyUnconnectedMagic(buffer);
        if (buffer.isReadable(8)) { // Server lists don't write this which causes errors. Please fix.
            serverId = buffer.readLong();
        }
    }
}
