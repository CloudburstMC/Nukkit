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

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    public int inventoryId;
    public int slot;
    public Item item;
    public int dynamicContainerId;

    @Override
    public void decode() {
    }

    @Override
    public void encode() {
        this.reset();
        this.putUnsignedVarInt(this.inventoryId);
        this.putUnsignedVarInt(this.slot);
        this.putUnsignedVarInt(this.dynamicContainerId);
        this.putSlot(this.item);
    }
}
