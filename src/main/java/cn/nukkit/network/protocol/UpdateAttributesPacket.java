package cn.nukkit.network.protocol;

import cn.nukkit.entity.Attribute;
import lombok.ToString;

/**
 * @author Nukkit Project Team
 */
@ToString
public class UpdateAttributesPacket extends DataPacket {

    public long entityRuntimeId;
    public Attribute[] entries;
    public long tick;

    @Override
    public byte pid() {
        return ProtocolInfo.UPDATE_ATTRIBUTES_PACKET;
    }

    public void decode() {
        this.entityRuntimeId = this.getEntityRuntimeId();
        this.entries = this.getAttributeList();
        this.tick = this.getUnsignedVarLong();
    }

    public void encode() {
        this.reset();
        this.putEntityRuntimeId(this.entityRuntimeId);
        this.putAttributeList(this.entries);
        this.putUnsignedVarLong(this.tick);
    }
}
