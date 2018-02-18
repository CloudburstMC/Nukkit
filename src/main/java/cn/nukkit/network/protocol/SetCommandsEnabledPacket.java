package cn.nukkit.network.protocol;

public class SetCommandsEnabledPacket extends DataPacket {

    public boolean enabled;

    @Override
    public byte pid(PlayerProtocol protocol) {
        return protocol.getPacketId("SET_COMMANDS_ENABLED_PACKET");
    }

    @Override
    public void decode(PlayerProtocol protocol) {

    }

    @Override
    public void encode(PlayerProtocol protocol) {
        this.reset(protocol);
        this.putBoolean(this.enabled);
    }
}
