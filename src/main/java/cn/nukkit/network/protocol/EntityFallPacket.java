package cn.nukkit.network.protocol;

public class EntityFallPacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.ENTITY_FALL_PACKET;

    public long eid;
    public float fallDistance;
    public boolean unknown;

    @Override
    public void decode() {
        this.eid = this.getEntityRuntimeId();
        this.fallDistance = this.getLFloat();
        this.unknown = this.getBoolean();
    }

    @Override
    public void encode() {
        this.putEntityRuntimeId(this.eid);
        this.putLFloat(this.fallDistance);
        this.putBoolean(this.unknown);
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }
}
