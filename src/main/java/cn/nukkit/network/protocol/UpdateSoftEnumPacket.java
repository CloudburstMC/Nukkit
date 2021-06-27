package cn.nukkit.network.protocol;

import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@ToString
public class UpdateSoftEnumPacket extends DataPacket {

    public static final byte TYPE_ADD = 0;
    public static final byte TYPE_REMOVE = 1;
    public static final byte TYPE_SET = 2;

    public String enumName;
    public String values = new String[0];
    public byte type;

    @Override
    public byte pid() {
        return ProtocolInfo.UPDATE_SOFT_ENUM_PACKET;
    }

    @Override
    public void decode() {
        this.enumName = this.getString();
        int count = (int) this.getUnsignedVarInt();
        for (int i = 0; i < count; i++) {
            this.values[i] = this.getString();
        }
        this.type = this.getByte();
    }

    @Override
    public void encode() {
        this.reset();
        this.putString(this.enumName);
        this.putUnsignedVarInt(this.values.length);
        for (String value : this.values) {
            this.putString(value);
        }
        this.putByte(this.type);
    }
}
