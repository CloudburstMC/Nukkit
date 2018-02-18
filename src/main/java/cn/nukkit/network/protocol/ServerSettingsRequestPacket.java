package cn.nukkit.network.protocol;

public class ServerSettingsRequestPacket extends DataPacket {

    @Override
    public byte pid(PlayerProtocol protocol) {
        return protocol.getPacketId("SERVER_SETTINGS_REQUEST_PACKET");
    }

    @Override
    public void decode(PlayerProtocol protocol) {

    }

    @Override
    public void encode(PlayerProtocol protocol) {

    }
}
