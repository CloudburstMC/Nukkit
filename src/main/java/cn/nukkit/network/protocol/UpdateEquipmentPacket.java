package cn.nukkit.network.protocol;

import cn.nukkit.nbt.tag.CompoundTag;
import lombok.ToString;

@ToString(exclude = "namedTag")
public class UpdateEquipmentPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.UPDATE_EQUIPMENT_PACKET;

    public int windowId;
    public int windowType;
    public int windowSlotCount;
    public long entityUniqueId;
    public CompoundTag namedTag;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.windowId = this.getByte();
        this.windowType = this.getByte();
        this.windowSlotCount = this.getVarInt();
        this.entityUniqueId = this.getEntityUniqueId();
        this.namedTag = this.getCompoundTag();
    }

    @Override
    public void encode() {
        this.reset();
        this.putByte((byte) this.windowId);
        this.putByte((byte) this.windowType);
        this.putVarInt(this.windowSlotCount);
        this.putEntityUniqueId(this.entityUniqueId);
        this.putCompoundTag(this.namedTag);
    }
}
