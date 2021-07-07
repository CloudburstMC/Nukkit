package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class SetHealthPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.SET_HEALTH_PACKET;

    public int health;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.health = this.getVarInt();
    }

    @Override
    public void encode() {
        this.reset();
        this.putVarInt(this.health);
    }
}
