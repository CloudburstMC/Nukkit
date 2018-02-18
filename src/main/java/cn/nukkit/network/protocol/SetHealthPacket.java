package cn.nukkit.network.protocol;

public class SetHealthPacket extends DataPacket {

    public int health;

    @Override
    public byte pid(PlayerProtocol protocol) {
        return protocol.getPacketId("SET_HEALTH_PACKET");
    }

    @Override
    public void decode(PlayerProtocol protocol) {

    }

    @Override
    public void encode(PlayerProtocol protocol) {
        this.reset(protocol);
        this.putUnsignedVarInt(this.health);
    }
}
