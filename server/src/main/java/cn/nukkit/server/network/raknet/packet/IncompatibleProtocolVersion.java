package cn.nukkit.server.network.raknet.packet;

import cn.nukkit.server.network.raknet.RakNetPacket;
import cn.nukkit.server.network.raknet.RakNetUtil;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class IncompatibleProtocolVersion implements RakNetPacket {
    private byte rakNetVersion;
    private long serverId;

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeByte(rakNetVersion);
        RakNetUtil.writeUnconnectedMagic(buffer);
        buffer.writeLong(serverId);
    }

    @Override
    public void decode(ByteBuf buffer) {
        rakNetVersion = buffer.readByte();
        RakNetUtil.verifyUnconnectedMagic(buffer);
        serverId = buffer.readLong();
    }
}
