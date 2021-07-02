package cn.nukkit.network.protocol;

import lombok.ToString;

/**
 * Created on 15-10-14.
 */
@ToString
public class TakeItemEntityPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.TAKE_ITEM_ENTITY_PACKET;

    public long targetRuntimeId;
    public long entityRuntimeId;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.targetRuntimeId = this.getEntityRuntimeId();
        this.entityRuntimeId = this.getEntityRuntimeId();
    }

    @Override
    public void encode() {
        this.reset();
        this.putEntityRuntimeId(this.targetRuntimeId);
        this.putEntityRuntimeId(this.entityRuntimeId);
    }
}
