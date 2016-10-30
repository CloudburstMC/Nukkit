package cn.nukkit.network.protocol;

import cn.nukkit.item.Item;

public class InventoryActionPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.INVENTORY_ACTION_PACKET;

    public int unknown;
    public Item item;

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        this.reset();
        this.putUnsignedVarInt(unknown);
        this.putSlot(item);
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }
}
