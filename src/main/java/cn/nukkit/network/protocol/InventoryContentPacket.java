package cn.nukkit.network.protocol;

import cn.nukkit.item.Item;
import lombok.ToString;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@ToString
public class InventoryContentPacket extends DataPacket {

    public static final int SPECIAL_INVENTORY = 0;
    public static final int SPECIAL_OFFHAND = 0x77;
    public static final int SPECIAL_ARMOR = 0x78;
    public static final int SPECIAL_CREATIVE = 0x79;
    public static final int SPECIAL_HOTBAR = 0x7a;
    public static final int SPECIAL_FIXED_INVENTORY = 0x7b;

    public int windowId;
    public Item[] items = new Item[0];

    @Override
    public byte pid() {
        return ProtocolInfo.INVENTORY_CONTENT_PACKET;
    }

    @Override
    public void decode() {
        this.windowId = (int) this.getUnsignedVarInt();
        int count = (int) this.getUnsignedVarInt();
        this.items = new Item[count];
        for (int i = 0; i < count; i++) {
            this.items[i] = this.getSlot();
        }
    }

    @Override
    public void encode() {
        this.reset();
        this.putUnsignedVarInt(this.windowId);
        this.putUnsignedVarInt(this.items.length);
        for (Item item : this.items) {
            this.putSlot(item);
        }
    }
}
