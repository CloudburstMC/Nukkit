package cn.nukkit.network.protocol;

import lombok.ToString;

/**
 * Created on 15-10-14.
 */
@ToString
public class TakeItemEntityPacket extends DataPacket {

    public long targetRuntimeId;
    public long entityRuntimeId;

    @Override
    public byte pid() {
        return ProtocolInfo.TAKE_ITEM_ENTITY_PACKET;
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
