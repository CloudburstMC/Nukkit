package cn.nukkit.network.protocol;

import io.netty.buffer.ByteBuf;
import lombok.ToString;

@ToString
public class InitiateWebSocketConnectionPacket extends DataPacket {

    @Override
    public short pid() {
        return ProtocolInfo.INITIATE_WEB_SOCKET_CONNECTION_PACKET;
    }

    @Override
    protected void decode(ByteBuf buffer) {

    }

    @Override
    protected void encode(ByteBuf buffer) {
        //TODO
    }
}
