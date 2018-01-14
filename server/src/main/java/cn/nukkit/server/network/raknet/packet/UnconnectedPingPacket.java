package cn.nukkit.server.network.raknet.packet;

import cn.nukkit.server.network.raknet.NetworkPacket;
import cn.nukkit.server.network.raknet.RakNetUtil;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class UnconnectedPingPacket implements NetworkPacket {
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
        if (buffer.readableBytes() > 0) { // Server lists don't write this which causes errors. Please fix.
            serverId = buffer.readLong();
        }
    }
}
