package cn.nukkit.network.protocol;

import io.netty.buffer.ByteBuf;
import lombok.ToString;

@ToString
public class ClientToServerHandshakePacket extends DataPacket {

    @Override
    public short pid() {
        return ProtocolInfo.CLIENT_TO_SERVER_HANDSHAKE_PACKET;
    }

    @Override
    protected void decode(ByteBuf buffer) {
        //no content
    }

    @Override
    protected void encode(ByteBuf buffer) {

    }
}
