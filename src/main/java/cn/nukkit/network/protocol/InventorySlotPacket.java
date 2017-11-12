package cn.nukkit.network.protocol;

import cn.nukkit.item.Item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class InventorySlotPacket extends DataPacket {

    @Override
    public byte pid(PlayerProtocol protocol) {
        return protocol.equals(PlayerProtocol.PLAYER_PROTOCOL_113) ?
                ProtocolInfo113.CONTAINER_SET_SLOT_PACKET :
                ProtocolInfo.INVENTORY_SLOT_PACKET;
    }

    public int inventoryId;
    public int slot;
    public Item item;
    //Only 1.1
    public int hotbarSlot;
    public int selectedSlot;

    @Override
    public void decode(PlayerProtocol protocol) {
        if (protocol.equals(PlayerProtocol.PLAYER_PROTOCOL_113)){
            this.inventoryId = this.getByte();
            this.slot = this.getVarInt();
            this.hotbarSlot = this.getVarInt();
            this.item = this.getSlot();
            this.selectedSlot = this.getByte();
            return;
        }
        this.inventoryId = (int) this.getUnsignedVarInt();
        this.slot = (int) this.getUnsignedVarInt();
        this.item = this.getSlot();
    }

    @Override
    public void encode(PlayerProtocol protocol) {
        this.reset(protocol);
        if (protocol.equals(PlayerProtocol.PLAYER_PROTOCOL_113)){
            this.putByte((byte) this.inventoryId);
            this.putVarInt(this.slot);
            this.putVarInt(this.hotbarSlot);
            this.putSlot(this.item);
            this.putByte((byte) this.selectedSlot);
            return;
        }
        this.putUnsignedVarInt((byte) this.inventoryId);
        this.putUnsignedVarInt(this.slot);
        this.putSlot(this.item);
    }
}
