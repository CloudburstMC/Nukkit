package cn.nukkit.network.protocol;

import lombok.ToString;

/**
 * Created on 15-10-22.
 */
@ToString
public class SetEntityLinkPacket extends DataPacket {

	public static final byte TYPE_REMOVE = 0;
	public static final byte TYPE_RIDE = 1;
	public static final byte TYPE_PASSENGER = 2;

	public long vehicleUniqueId; //From
	public long riderUniqueId; //To
	public byte type;
	public boolean immediate;
	public boolean riderInitiated;

	@Override
	public byte pid() {
		return ProtocolInfo.SET_ENTITY_LINK_PACKET;
	}

	@Override
	public void decode() {
		this.vehicleUniqueId = this.getEntityUniqueId();
		this.riderUniqueId = this.getEntityUniqueId();
		this.type = this.getByte();
		this.immediate = this.getBoolean();
		this.riderInitiated = this.getBoolean();
	}

	@Override
	public void encode() {
		this.reset();
		this.putEntityUniqueId(this.vehicleUniqueId);
		this.putEntityUniqueId(this.riderUniqueId);
		this.putByte(this.type);
		this.putBoolean(this.immediate);
		this.putBoolean(this.riderInitiated);
	}
}
