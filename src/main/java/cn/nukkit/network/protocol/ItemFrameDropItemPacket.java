package cn.nukkit.network.protocol;

import cn.nukkit.item.Item;
import cn.nukkit.math.BlockVector3;

/**
 * Created by Pub4Game on 03.07.2016.
 */
public class ItemFrameDropItemPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.ITEM_FRAME_DROP_ITEM_PACKET;

    public int x;
    public int y;
    public int z;
    public Item dropItem;

    @Override
    public void decode() {
        BlockVector3 v = this.getBlockCoords();
        this.z = v.x;
        this.y = v.y;
        this.x = v.z;
        this.dropItem = this.getSlot();
    }

    @Override
    public void encode() {

    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }
}