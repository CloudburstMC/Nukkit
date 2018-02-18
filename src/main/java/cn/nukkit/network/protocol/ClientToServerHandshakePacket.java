package cn.nukkit.network.protocol;

public class ClientToServerHandshakePacket extends DataPacket {

    @Override
    public byte pid(PlayerProtocol protocol) {
        return protocol.getPacketId("CLIENT_TO_SERVER_HANDSHAKE_PACKET");
    }

    @Override
    public void decode(PlayerProtocol protocol) {
        //no content
    }

    @Override
    public void encode(PlayerProtocol protocol) {

    }
}
