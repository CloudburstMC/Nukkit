package cn.nukkit.network.protocol;

import lombok.ToString;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@ToString
public class RemoveEntityPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.REMOVE_ENTITY_PACKET;

    public long entityUniqueId;

    @Override
    public byte pid() {
        return ProtocolInfo.REMOVE_ENTITY_PACKET;
    }

    @Override
    public void decode() {
        this.entityUniqueId = this.getEntityUniqueId();
    }

    @Override
    public void encode() {
        this.reset();
        this.putEntityUniqueId(this.entityUniqueId);
    }
}
