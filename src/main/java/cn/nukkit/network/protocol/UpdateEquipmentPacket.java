package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString(exclude = "namedtag")
public class UpdateEquipmentPacket extends DataPacket {

    public byte windowId;
    public byte windowType;
    public int windowSlotCount; //TODO: find out what this is (vanilla always sends 0)
    public long entityId;
    public byte[] namedTag;

    @Override
    public byte pid() {
        return ProtocolInfo.UPDATE_EQUIPMENT_PACKET;
    }

    @Override
    public void decode() {
    	this.windowId = this.getByte();
		this.windowType = this.getByte();
		this.windowSlotCount = this.getVarInt();
		this.entityId = this.getEntityUniqueId();
		//this.namedTag idk
    }

    @Override
    public void encode() {
        this.reset();
        this.putByte(this.windowId);
        this.putByte(this.windowType);
		this.putVarInt(this.windowSlotCount);
		this.putEntityUniqueId(this.entityId);
		this.put(this.namedTag);
    }
}
