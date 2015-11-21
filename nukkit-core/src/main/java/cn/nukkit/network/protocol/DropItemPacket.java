package cn.nukkit.network.protocol;

import cn.nukkit.item.Item;

/**
 * @author Nukkit Project Team
 */
public class DropItemPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.DROP_ITEM_PACKET;

    public int type;
    public Item item;

    @Override
    public void decode() {
        type = getByte();
        item = getSlot();
    }

    @Override
    public void encode() {

    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

}
