package cn.nukkit.network.protocol;

import cn.nukkit.item.Item;
import lombok.ToString;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@ToString
public class MobEquipmentPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.MOB_EQUIPMENT_PACKET;

    public long entityRuntimeId;
    public Item item;
    public byte inventorySlot;
    public byte hotbarSlot;
    public byte windowId;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.entityRuntimeId = this.getEntityRuntimeId();
        this.item = this.getSlot();
        this.inventorySlot = (byte) this.getByte();
        this.hotbarSlot = (byte) this.getByte();
        this.windowId = (byte) this.getByte();
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
