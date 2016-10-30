package cn.nukkit.network.protocol;

import cn.nukkit.math.BlockVector3;

public class AddHangingEntityPacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.ADD_HANGING_ENTITY_PACKET;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    public long entityUniqueId;
    public long entityRuntimeId;
    public int x;
    public int y;
    public int z;
    public int unknown;

    @Override
    public void decode() {
        this.entityUniqueId = this.getVarLong();
        this.entityRuntimeId = this.getEntityId();
        BlockVector3 v3 = this.getBlockCoords();
        this.x = v3.x;
        this.y = v3.y;
        this.z = v3.z;
        this.unknown = this.getVarInt();
    }

    @Override
    public void encode() {
        this.reset();
        this.putVarLong(this.entityUniqueId);
        this.putEntityId(this.entityRuntimeId);
        this.putBlockCoords(x, y, z);
        this.putVarInt(unknown);
    }
}
