package cn.nukkit.network.protocol;

public class ClientToServerHandshakePacket extends DataPacket {

    @Override
    public byte pid(PlayerProtocol protocol) {
        return protocol.equals(PlayerProtocol.PLAYER_PROTOCOL_113) ?
                ProtocolInfo113.CLIENT_TO_SERVER_HANDSHAKE_PACKET :
                ProtocolInfo.CLIENT_TO_SERVER_HANDSHAKE_PACKET;
    }

    @Override
    public void decode(PlayerProtocol protocol) {
        //no content
    }

    @Override
    public void encode(PlayerProtocol protocol) {

    }
}
