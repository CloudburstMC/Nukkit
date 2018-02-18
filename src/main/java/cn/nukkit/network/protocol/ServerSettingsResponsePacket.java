package cn.nukkit.network.protocol;

public class ServerSettingsResponsePacket extends DataPacket {

    public int formId;
    public String data;

    @Override
    public byte pid(PlayerProtocol protocol) {
        return protocol.getPacketId("SERVER_SETTINGS_RESPONSE_PACKET");
    }

    @Override
    public void decode(PlayerProtocol protocol) {

    }

    @Override
    public void encode(PlayerProtocol protocol) {
        this.reset(protocol);
        this.putVarInt(this.formId);
        this.putString(this.data);
    }
}
