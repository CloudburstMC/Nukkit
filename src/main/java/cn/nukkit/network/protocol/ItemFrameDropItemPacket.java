package cn.nukkit.network.protocol;

import cn.nukkit.math.BlockVector3;

/**
 * Created by Pub4Game on 03.07.2016.
 */
public class ItemFrameDropItemPacket extends DataPacket {

    public int x;
    public int y;
    public int z;

    @Override
    public byte pid(PlayerProtocol protocol) {
        return protocol.getPacketId("ITEM_FRAME_DROP_ITEM_PACKET");
    }

    @Override
    public void decode(PlayerProtocol protocol) {
        BlockVector3 v = this.getBlockVector3();
        this.z = v.z;
        this.y = v.y;
        this.x = v.x;
    }

    @Override
    public void encode(PlayerProtocol protocol) {

    }

}
