package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class ServerToClientHandshakePacket extends DataPacket {

    @Override
    public byte pid() {
        return ProtocolInfo.SERVER_TO_CLIENT_HANDSHAKE_PACKET;
    }

    public String jwt;

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        this.reset();
        this.putString(this.jwt);
    }
}
