package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class PositionTrackingDBClientRequestPacket extends DataPacket {

	public static final byte ACTION_QUERY = 0;

	public byte action;
	public int trackingId;

    @Override
    public byte pid() {
        return ProtocolInfo.POSITION_TRACKING_D_B_CLIENT_REQUEST_PACKET;
    }

    @Override
    public void decode() {
    	this.action = this.getByte();
		this.trackingId = this.getVarInt();
    }

    @Override
    public void encode() {
    	this.reset();
        this.putByte(this.action);
		this.putVarInt(this.trackingId);
    }
}
