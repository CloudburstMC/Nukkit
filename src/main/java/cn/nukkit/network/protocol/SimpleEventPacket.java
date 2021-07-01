package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class SimpleEventPacket extends DataPacket {

	public static final short TYPE_ENABLE_COMMANDS = 1;
	public static final short TYPE_DISABLE_COMMANDS = 2;
	public static final short TYPE_UNLOCK_WORLD_TEMPLATE_SETTINGS = 3;

    public short eventId;

    @Override
    public byte pid() {
        return ProtocolInfo.SIMPLE_EVENT_PACKET;
    }

    @Override
    public void decode() {
    	this.eventId = this.getShort();
    }

    @Override
    public void encode() {
        this.reset();
        this.putShort(this.eventId);
    }
}
