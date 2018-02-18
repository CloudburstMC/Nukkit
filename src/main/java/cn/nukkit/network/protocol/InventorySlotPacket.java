package cn.nukkit.network.protocol;

import cn.nukkit.item.Item;
import cn.nukkit.network.protocol.types.ContainerIds;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class InventorySlotPacket extends DataPacket {

    @Override
    public byte pid(PlayerProtocol protocol) {
        return protocol.getPacketId("INVENTORY_SLOT_PACKET");
    }

    public int inventoryId;
    public int slot;
    public Item item;
    //Only 1.1
    public int hotbarSlot;
    public int selectedSlot;

    @Override
    public void decode(PlayerProtocol protocol) {
        switch (protocol.getMainNumber()){
            case 130:
            default:
                this.inventoryId = (int) this.getUnsignedVarInt();
                this.slot = (int) this.getUnsignedVarInt();
                this.item = this.getSlot();
                return;
            case 113:
                this.inventoryId = this.getByte();
                this.slot = this.getVarInt()-9;
                if (this.slot < 0 || (this.inventoryId != 0 && this.inventoryId != ContainerIds.HOTBAR)) this.slot += 9;
                this.hotbarSlot = this.getVarInt();
                this.item = this.getSlot();
                this.selectedSlot = this.getByte();
                return;
        }
    }

    @Override
    public void encode(PlayerProtocol protocol) {
        this.reset(protocol);
        switch (protocol.getMainNumber()){
            case 130:
            default:
                this.putUnsignedVarInt((byte) this.inventoryId);
                this.putUnsignedVarInt(this.slot);
                this.putSlot(this.item);
                return;
            case 113:
                this.putByte((byte) this.inventoryId);
                this.putVarInt(this.slot);
                this.putVarInt(this.hotbarSlot);
                this.putSlot(this.item);
                this.putByte((byte) (this.selectedSlot));
                return;
        }
    }
}
