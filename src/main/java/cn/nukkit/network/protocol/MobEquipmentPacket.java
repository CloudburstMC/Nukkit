package cn.nukkit.network.protocol;

import cn.nukkit.item.Item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class MobEquipmentPacket extends DataPacket {

    @Override
    public byte pid(PlayerProtocol protocol) {
        return protocol.getPacketId("MOB_EQUIPMENT_PACKET");
    }

    public long eid;
    public Item item;
    public int inventorySlot;
    public int hotbarSlot;
    public int windowId;

    @Override
    public void decode(PlayerProtocol protocol) {
        this.eid = this.getEntityRuntimeId(); //EntityRuntimeID
        this.item = this.getSlot();
        this.inventorySlot = this.getByte();
        if (this.windowId == 0 && protocol.getMainNumber() == 113) this.inventorySlot -= 9;
        if (this.inventorySlot < 0) this.inventorySlot += 9;
        this.hotbarSlot = this.getByte();
        this.windowId = this.getByte();
    }

    @Override
    public void encode(PlayerProtocol protocol) {
        this.reset(protocol);
        this.putEntityRuntimeId(this.eid); //EntityRuntimeID
        this.putSlot(this.item);
        if (this.windowId == 0 && protocol.getMainNumber() == 113) this.inventorySlot += 9;
        if (this.inventorySlot < 0) this.inventorySlot += 9;
        this.putByte((byte) (this.inventorySlot));
        this.putByte((byte) this.hotbarSlot);
        this.putByte((byte) this.windowId);
    }
}
