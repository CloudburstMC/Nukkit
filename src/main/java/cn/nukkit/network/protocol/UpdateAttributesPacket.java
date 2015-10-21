package cn.nukkit.network.protocol;

import cn.nukkit.entity.Attribute;

/**
 * @author Nukkit Project Team
 */
public class UpdateAttributesPacket extends DataPacket {

    public static final byte NETWORK_ID = Info.UPDATE_ATTRIBUTES_PACKET;

    public Attribute[] entries;
    public long entityId;


    public void decode() {
        
    }

    public void encode() {
        reset();
        putLong(entityId);
        if (entries == null) {
            putShort(0);
        } else {
            putShort(entries.length);
            for (Attribute entry : entries) {
                putFloat(entry.getMinValue());
                putFloat(entry.getMaxValue());
                putFloat(entry.getValue());
                putString(entry.getName());
            }
        }
    }

    @Override
    public byte pid() {
        return 0;
    }

}
