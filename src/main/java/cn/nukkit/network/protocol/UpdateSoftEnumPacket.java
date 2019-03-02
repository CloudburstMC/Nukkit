package cn.nukkit.network.protocol;

public class UpdateSoftEnumPacket extends DataPacket {

    public String[] values = new String[0];
    public String name = "";
    public Type type = Type.SET;

    @Override
    public byte pid() {
        return ProtocolInfo.UPDATE_SOFT_ENUM_PACKET;
    }

    @Override
    public void decode() {
        this.name = this.getString();
        this.values = new String[(int) this.getUnsignedVarInt()];

        for(int i = 0; i < this.values.length; i++) {
            this.values[i] = this.getString();
        }
        this.type = Type.values()[this.getByte()];
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
