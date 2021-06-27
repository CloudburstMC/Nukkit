package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class SetHealthPacket extends DataPacket {

    public int health;

    @Override
    public byte pid() {
        return ProtocolInfo.SET_HEALTH_PACKET;
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
