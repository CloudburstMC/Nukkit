package cn.nukkit.network.protocol;

public class ServerToClientHandshakePacket extends DataPacket {

    @Override
    public byte pid(PlayerProtocol protocol) {
        return protocol.getPacketId("SERVER_TO_CLIENT_HANDSHAKE_PACKET");
    }

    public String publicKey;
    public String serverToken;
    public String privateKey;

    @Override
    public void decode(PlayerProtocol protocol) {

    }

    @Override
    public void encode(PlayerProtocol protocol) {
        //TODO
    }
}
