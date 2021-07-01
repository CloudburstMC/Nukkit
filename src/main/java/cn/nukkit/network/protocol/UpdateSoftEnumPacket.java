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
	public List<String> values = new ArrayList<>();
	public byte type;

    @Override
    public byte pid() {
        return ProtocolInfo.UPDATE_SOFT_ENUM_PACKET;
    }

    @Override
    public void decode() {
    	this.enumName = this.getString();
		for (int i = 0, count = this.getUnsignedVarInt(); i < count; i++) {
			this.values.add(this.getString());
		}
		this.type = this.getByte();
    }

    @Override
    public void encode() {
        this.reset();
        this.putString(this.enumName);
		this.putUnsignedVarInt(this.values.size());
		for (String value : this.values) {
			this.putString(value);
		}
		this.putByte(this.type);
    }
}
