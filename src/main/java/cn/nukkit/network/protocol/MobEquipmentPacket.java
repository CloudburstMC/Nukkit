package cn.nukkit.network.protocol;

import cn.nukkit.item.Item;
import lombok.ToString;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@ToString
public class MobEquipmentPacket extends DataPacket {

    @Override
    public byte pid() {
        return ProtocolInfo.MOB_EQUIPMENT_PACKET;
    }

    public long entityRuntimeId;
    public Item item;
    public byte inventorySlot;
    public byte hotbarSlot;
    public byte windowId;

    @Override
    public void decode() {
        this.entityRuntimeId = this.getEntityRuntimeId();
        this.item = this.getSlot();
        this.inventorySlot = this.getByte();
        this.hotbarSlot = this.getByte();
        this.windowId = this.getByte();
    }

    @Override
    public void encode() {
        this.reset();
        this.putEntityRuntimeId(this.entityRuntimeId);
        this.putSlot(this.item);
        this.putByte(this.inventorySlot);
        this.putByte(this.hotbarSlot);
        this.putByte(this.windowId);
    }
}
