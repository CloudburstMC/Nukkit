package cn.nukkit.network.protocol;

import cn.nukkit.item.Item;

public class InventoryActionPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.INVENTORY_ACTION_PACKET;

    public int actionId;
    public Item item;
    public int enchantmentId;
    public int enchantmentLevel;

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        this.reset();
        this.putUnsignedVarInt(this.actionId);
        this.putSlot(this.item);
        this.putVarInt(this.enchantmentId);
        this.putVarInt(this.enchantmentLevel);
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }
}
