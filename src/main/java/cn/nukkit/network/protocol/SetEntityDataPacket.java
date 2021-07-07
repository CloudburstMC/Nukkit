package cn.nukkit.network.protocol;

import cn.nukkit.entity.data.EntityMetadata;
import lombok.ToString;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@ToString
public class SetEntityDataPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.SET_ENTITY_DATA_PACKET;

    public long entityRuntimeId;
    public EntityMetadata entityMetadata;
    public long tick;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.entityRuntimeId = this.getEntityRuntimeId();
        this.entityMetadata = this.getMetadata();
        this.tick = this.getUnsignedVarLong();
    }

    @Override
    public void encode() {
        this.reset();
        this.putEntityRuntimeId(this.entityRuntimeId);
        this.putMetadata(this.entityMetadata);
        this.putUnsignedVarLong(this.tick);
    }
}
