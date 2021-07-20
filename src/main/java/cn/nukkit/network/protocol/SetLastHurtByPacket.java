package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class SetLastHurtByPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.SET_LAST_HURT_BY_PACKET;

    public int entityTypeId;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.entityTypeId = this.getVarInt();
    }

    @Override
    public void encode() {
        this.reset();
        this.putVarInt(this.entityTypeId);
    }
}
