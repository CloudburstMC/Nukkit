package cn.nukkit.network.protocol;

import cn.nukkit.entity.Attribute;
import cn.nukkit.utils.Binary;
import io.netty.buffer.ByteBuf;
import lombok.ToString;

/**
 * @author Nukkit Project Team
 */
@ToString
public class UpdateAttributesPacket extends DataPacket {

    public static final short NETWORK_ID = ProtocolInfo.UPDATE_ATTRIBUTES_PACKET;

    public Attribute[] entries;
    public long entityId;

    @Override
    public short pid() {
        return NETWORK_ID;
    }

    protected void decode(ByteBuf buffer) {

    }

    protected void encode(ByteBuf buffer) {

        Binary.writeEntityRuntimeId(buffer, this.entityId);

        if (this.entries == null) {
            Binary.writeUnsignedVarInt(buffer, 0);
        } else {
            Binary.writeUnsignedVarInt(buffer, this.entries.length);
            for (Attribute entry : this.entries) {
                buffer.writeFloatLE(entry.getMinValue());
                buffer.writeFloatLE(entry.getMaxValue());
                buffer.writeFloatLE(entry.getValue());
                buffer.writeFloatLE(entry.getDefaultValue());
                Binary.writeString(buffer, entry.getName());
            }
        }
    }

}
