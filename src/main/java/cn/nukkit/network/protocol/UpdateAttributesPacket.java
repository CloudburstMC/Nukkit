package cn.nukkit.network.protocol;

import cn.nukkit.entity.Attribute;

/**
 * @author Nukkit Project Team
 */
public class UpdateAttributesPacket extends DataPacket {

    public static final byte NETWORK_ID = Info.UPDATE_ATTRIBUTES_PACKET;

    public Attribute[] entries;
    public long entityId;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    public void decode() {

    }

    public void encode() {
        this.reset();

        this.putLong(this.entityId);

        if (this.entries == null) {
            this.putShort(0);
        } else {
            this.putShort(this.entries.length);
            for (Attribute entry : this.entries) {
                this.putFloat(entry.getMinValue());
                this.putFloat(entry.getMaxValue());
                this.putFloat(entry.getValue());
                this.putString(entry.getName());
            }
        }
    }

}
