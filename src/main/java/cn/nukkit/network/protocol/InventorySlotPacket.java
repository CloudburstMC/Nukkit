package cn.nukkit.network.protocol;

import cn.nukkit.item.Item;
import lombok.ToString;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@ToString
public class InventorySlotPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.INVENTORY_SLOT_PACKET;

    public int windowId;
    public int inventorySlot;
    public Item item;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.windowId = (int) this.getUnsignedVarInt();
        this.inventorySlot = (int) this.getUnsignedVarInt();
        this.item = this.getSlot();
    }

    @Override
    public void encode() {
        this.reset();
        this.putUnsignedVarInt(this.windowId);
        this.putUnsignedVarInt(this.inventorySlot);
        this.putSlot(this.item);
    }
}
