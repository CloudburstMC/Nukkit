package cn.nukkit.network.protocol;

import cn.nukkit.item.Item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class InventoryContentPacket extends DataPacket {

    @Override
    public byte pid(PlayerProtocol protocol) {
        return protocol.equals(PlayerProtocol.PLAYER_PROTOCOL_113) ?
                ProtocolInfo113.CONTAINER_SET_CONTENT_PACKET :
                ProtocolInfo.INVENTORY_CONTENT_PACKET;
    }

    public static final int SPECIAL_INVENTORY = 0;
    public static final int SPECIAL_OFFHAND = 0x77;
    public static final int SPECIAL_ARMOR = 0x78;
    public static final int SPECIAL_CREATIVE = 0x79;
    public static final int SPECIAL_HOTBAR = 0x7a;
    public static final int SPECIAL_FIXED_INVENTORY = 0x7b;

    public int inventoryId;
    public long eid = -1;
    public Item[] slots = new Item[0];
    //Only 1.1 clients
    public int[] hotbar = new int[0];

    @Override
    public DataPacket clean() {
        this.slots = new Item[0];
        this.hotbar = new int[0];
        return super.clean();
    }

    @Override
    public void decode(PlayerProtocol protocol) {
        this.inventoryId = (int) this.getUnsignedVarInt();
        if (protocol.equals(PlayerProtocol.PLAYER_PROTOCOL_113)){
            this.eid = this.getUnsignedVarLong();
        }
        int count = (int) this.getUnsignedVarInt();
        this.slots = new Item[count];

        for (int s = 0; s < count && !this.feof(); ++s) {
            this.slots[s] = this.getSlot();
        }

        if (protocol.equals(PlayerProtocol.PLAYER_PROTOCOL_113)) {
            int hotbar = (int) this.getUnsignedVarInt();
            this.hotbar = new int[count];

            for (int s = 0; s < count && !this.feof(); ++s) {
                this.hotbar[s] = this.getVarInt();
            }
        }
    }

    @Override
    public void encode(PlayerProtocol protocol) {
        if (protocol.equals(PlayerProtocol.PLAYER_PROTOCOL_113)) {
            this.reset(protocol);
            this.putUnsignedVarInt(this.inventoryId);
            this.putEntityUniqueId(this.eid);
            this.putUnsignedVarInt(this.slots.length);
            for (Item slot : this.slots){
                this.putSlot(slot);
            }
            if (this.inventoryId == SPECIAL_INVENTORY) {
                this.putUnsignedVarInt(this.hotbar.length);
                for (int i : this.hotbar){
                    this.putVarInt(i);
                }
            }
            else this.putUnsignedVarInt(0);
        }
        else {
            this.reset(protocol);
            this.putUnsignedVarInt(this.inventoryId);
            this.putUnsignedVarInt(this.slots.length);
            for (Item slot : this.slots) {
                this.putSlot(slot);
            }
        }

    }

    @Override
    public InventoryContentPacket clone() {
        InventoryContentPacket pk = (InventoryContentPacket) super.clone();
        pk.inventoryId = this.inventoryId;
        pk.eid = this.eid;
        pk.slots = this.slots.clone();
        pk.hotbar = this.hotbar.clone();
        return pk;
    }
}
