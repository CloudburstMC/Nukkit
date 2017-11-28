package cn.nukkit.network.protocol;

public class ClientToServerHandshakePacket extends DataPacket {

    @Override
    public byte pid() {
        return ProtocolInfo.CLIENT_TO_SERVER_HANDSHAKE_PACKET;
    }

    @Override
    public void decode() {
        //no content
    }

    @Override
    public void encode() {

    }
}
