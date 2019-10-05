package cn.nukkit.network.protocol;

import cn.nukkit.utils.Binary;
import io.netty.buffer.ByteBuf;
import lombok.ToString;

@ToString
public class UpdateSoftEnumPacket extends DataPacket {

    public final String[] values = new String[0];
    public String name = "";
    public Type type = Type.SET;

    @Override
    public short pid() {
        return ProtocolInfo.UPDATE_SOFT_ENUM_PACKET;
    }

    @Override
    protected void decode(ByteBuf buffer) {
    }

    @Override
    protected void encode(ByteBuf buffer) {
        Binary.writeString(buffer, name);
        Binary.writeUnsignedVarInt(buffer, values.length);

        for (String value : values) {
            Binary.writeString(buffer, value);
        }
        buffer.writeByte(type.ordinal());
    }

    public enum Type {
        ADD,
        REMOVE,
        SET
    }
}
