package cn.nukkit.network.protocol;

public class AddHangingEntityPacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.ADD_HANGING_ENTITY_PACKET;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    public long entityUniqueId;
    public long entityRuntimeId;
    public float x;
    public float y;
    public float z;
    public int unknown;

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        this.reset();
        this.putEntityId(this.entityUniqueId);
        this.putEntityId(this.entityRuntimeId);
        this.putVector3f(x, y, z);
        this.putVarInt(unknown);
    }
}
