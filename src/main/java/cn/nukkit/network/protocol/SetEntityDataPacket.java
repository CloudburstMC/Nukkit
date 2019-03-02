package cn.nukkit.network.protocol;

import cn.nukkit.entity.data.EntityMetadata;
import cn.nukkit.utils.Binary;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class SetEntityDataPacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.SET_ENTITY_DATA_PACKET;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    public long eid;
    public EntityMetadata metadata;

    @Override
    public void decode() {
        this.eid = this.getEntityRuntimeId();
        this.metadata = this.getMetadata();
    }

    @Override
    public void encode() {
        this.reset();
        this.putEntityRuntimeId(this.eid);
        this.putMetadata(this.metadata);
    }
}
