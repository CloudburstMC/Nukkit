package cn.nukkit.network.protocol;

public class SubClientLoginPacket extends DataPacket {

    @Override
    public byte pid(PlayerProtocol protocol) {
        return protocol.getPacketId("SUB_CLIENT_LOGIN_PACKET");
    }

    @Override
    public void decode(PlayerProtocol protocol) {

    }

    @Override
    public void encode(PlayerProtocol protocol) {
        //TODO
    }
}
