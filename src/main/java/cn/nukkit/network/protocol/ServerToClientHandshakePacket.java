package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class ServerToClientHandshakePacket extends DataPacket {

    public String jwt;

    @Override
    public byte pid() {
        return ProtocolInfo.SERVER_TO_CLIENT_HANDSHAKE_PACKET;
    }

    @Override
    public void decode() {
        this.jwt = this.getString();
    }

    @Override
    public void encode() {
        this.reset();
        this.putString(this.jwt);
    }
}
