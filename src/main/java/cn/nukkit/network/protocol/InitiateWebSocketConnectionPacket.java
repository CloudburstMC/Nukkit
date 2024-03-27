package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class InitiateWebSocketConnectionPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.INITIATE_WEB_SOCKET_CONNECTION_PACKET;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.decodeUnsupported();
    }

    @Override
    public void encode() {
        this.encodeUnsupported();
    }
}
