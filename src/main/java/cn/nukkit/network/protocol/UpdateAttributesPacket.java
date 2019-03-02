package cn.nukkit.network.protocol;

import cn.nukkit.entity.Attribute;

/**
 * @author Nukkit Project Team
 */
public class UpdateAttributesPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.UPDATE_ATTRIBUTES_PACKET;

    public Attribute[] entries;
    public long entityId;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    public void decode() {
        this.entityId = this.getEntityRuntimeId();
        try {
            this.entries = this.getAttributeList();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void encode() {
        this.reset();
        this.putEntityRuntimeId(this.entityId);
        this.putAttributeList(this.entries);
    }

}
