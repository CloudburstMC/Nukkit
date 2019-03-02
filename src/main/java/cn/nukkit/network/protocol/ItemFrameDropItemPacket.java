package cn.nukkit.network.protocol;

import cn.nukkit.math.BlockVector3;

/**
 * Created by Pub4Game on 03.07.2016.
 */
public class ItemFrameDropItemPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.ITEM_FRAME_DROP_ITEM_PACKET;

    public int x;
    public int y;
    public int z;

    @Override
    public void decode() {
        BlockVector3 v = this.getBlockVector3();
        this.z = v.z;
        this.y = v.y;
        this.x = v.x;
    }

    @Override
    public void encode() {
        this.reset();
        this.putBlockVector3(this.x, this.y, this.z);
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }
}
