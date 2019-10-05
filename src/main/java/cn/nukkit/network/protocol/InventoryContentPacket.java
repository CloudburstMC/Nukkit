package cn.nukkit.network.protocol;

import cn.nukkit.item.Item;
import cn.nukkit.utils.Binary;
import io.netty.buffer.ByteBuf;
import lombok.ToString;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@ToString(exclude = {"slots"})
public class InventoryContentPacket extends DataPacket {
    public static final short NETWORK_ID = ProtocolInfo.INVENTORY_CONTENT_PACKET;

    @Override
    public short pid() {
        return NETWORK_ID;
    }

    public static final int SPECIAL_INVENTORY = 0;
    public static final int SPECIAL_OFFHAND = 0x77;
    public static final int SPECIAL_ARMOR = 0x78;
    public static final int SPECIAL_CREATIVE = 0x79;
    public static final int SPECIAL_HOTBAR = 0x7a;
    public static final int SPECIAL_FIXED_INVENTORY = 0x7b;

    public int inventoryId;
    public Item[] slots = new Item[0];

    @Override
    protected void decode(ByteBuf buffer) {
        this.inventoryId = (int) Binary.readUnsignedVarInt(buffer);
        int count = (int) Binary.readUnsignedVarInt(buffer);
        this.slots = new Item[count];

        for (int s = 0; s < count; ++s) {
            this.slots[s] = Binary.readItem(buffer);
        }
    }

    @Override
    protected void encode(ByteBuf buffer) {
        Binary.writeUnsignedVarInt(buffer, this.inventoryId);
        Binary.writeUnsignedVarInt(buffer, this.slots.length);
        for (Item slot : this.slots) {
            Binary.writeItem(buffer, slot);
        }
    }

    @Override
    public InventoryContentPacket clone() {
        InventoryContentPacket pk = (InventoryContentPacket) super.clone();
        pk.slots = this.slots.clone();
        return pk;
    }
}
