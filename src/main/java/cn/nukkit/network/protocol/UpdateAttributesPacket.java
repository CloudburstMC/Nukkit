package cn.nukkit.network.protocol;

import cn.nukkit.entity.Attribute;
import lombok.ToString;

/**
 * @author Nukkit Project Team
 */
@ToString
public class UpdateAttributesPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.UPDATE_ATTRIBUTES_PACKET;

    public long entityRuntimeId;
    public Attribute[] entries = new Attribute[0];
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
