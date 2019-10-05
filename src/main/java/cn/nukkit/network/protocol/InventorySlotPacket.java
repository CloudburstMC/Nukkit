package cn.nukkit.network.protocol;

import cn.nukkit.item.Item;
import cn.nukkit.utils.Binary;
import io.netty.buffer.ByteBuf;
import lombok.ToString;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@ToString
public class InventorySlotPacket extends DataPacket {
    public static final short NETWORK_ID = ProtocolInfo.INVENTORY_SLOT_PACKET;

    @Override
    public short pid() {
        return NETWORK_ID;
    }

    public int inventoryId;
    public int slot;
    public Item item;

    @Override
    protected void decode(ByteBuf buffer) {
        this.inventoryId = (int) Binary.readUnsignedVarInt(buffer);
        this.slot = (int) Binary.readUnsignedVarInt(buffer);
        this.item = Binary.readItem(buffer);
    }

    @Override
    protected void encode(ByteBuf buffer) {
        Binary.writeUnsignedVarInt(buffer, (byte) this.inventoryId);
        Binary.writeUnsignedVarInt(buffer, this.slot);
        Binary.writeItem(buffer, this.item);
    }
}
