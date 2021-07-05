package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class EntityFallPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.ENTITY_FALL_PACKET;

    public long entityRuntimeId;
    public float fallDistance;
    public boolean unknown;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.entityRuntimeId = this.getEntityRuntimeId();
        this.fallDistance = this.getLFloat();
        this.unknown = this.getBoolean();
    }

    @Override
    public void encode() {
        this.reset();
        this.putEntityRuntimeId(this.entityRuntimeId);
        this.putLFloat(this.fallDistance);
        this.putBoolean(this.unknown);
    }
}
