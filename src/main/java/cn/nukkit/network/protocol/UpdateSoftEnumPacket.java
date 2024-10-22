package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class UpdateSoftEnumPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.UPDATE_SOFT_ENUM_PACKET;

    public final String[] values = new String[0];
    public String name = "";
    public Type type = Type.SET;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.decodeUnsupported();
    }

    @Override
    public void encode() {
        this.reset();
        this.putString(name);
        this.putUnsignedVarInt(values.length);

        for (String value : values) {
            this.putString(value);
        }
        this.putByte((byte) type.ordinal());
    }

    public enum Type {
        ADD,
        REMOVE,
        SET
    }
}
