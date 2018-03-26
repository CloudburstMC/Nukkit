package cn.nukkit.server.network.raknet.packet;

import cn.nukkit.server.network.raknet.RakNetPacket;
import cn.nukkit.server.network.raknet.RakNetUtil;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class OpenConnectionRequest1Packet implements RakNetPacket {
    private byte protocolVersion;
    private short mtu;

    @Override
    public void encode(ByteBuf buffer) {
        RakNetUtil.writeUnconnectedMagic(buffer);
        buffer.writeByte(protocolVersion);
        buffer.writeBytes(new byte[mtu - 18]);
    }

    @Override
    public void decode(ByteBuf buffer) {
        RakNetUtil.verifyUnconnectedMagic(buffer);
        protocolVersion = buffer.readByte();
        mtu = (short) (buffer.readableBytes() + 18);
        buffer.skipBytes(buffer.readableBytes());
    }
}
