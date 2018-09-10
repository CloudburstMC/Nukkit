package cn.nukkit.network.protocol;

public class UpdateSoftEnumPacket extends DataPacket {

    public final String[] values = new String[0];
    public String name = "";
    public Type type = Type.SET;

    @Override
    public byte pid() {
        return ProtocolInfo.UPDATE_SOFT_ENUM_PACKET;
    }

    @Override
    public void decode() {
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
