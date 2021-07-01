package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class EntityPickRequestPacket extends DataPacket {

    public long entityId;
	public byte hotbarSlot;

    @Override
    public byte pid() {
        return ProtocolInfo.ENTITY_PICK_REQUEST_PACKET;
    }

    @Override
    public void decode() {
    	this.entityId = this.getEntityRuntimeId();
        this.hotbarSlot = this.getByte();
    }

    @Override
    public void encode() {
    	this.reset();
        this.putEntityRuntimeId(this.entityId);
        this.putByte(this.hotbarSlot);
    }
}
