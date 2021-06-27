package cn.nukkit.network.protocol;

import lombok.ToString;

/**
 * Created by Pub4Game on 03.07.2016.
 */
@ToString
public class ItemFrameDropItemPacket extends DataPacket {

    public int x;
    public int y;
    public int z;

    @Override
    public byte pid() {
        return ProtocolInfo.ITEM_FRAME_DROP_ITEM_PACKET;
    }

    @Override
    public void decode() {
        this.getBlockVector3(this.x, this.y, this.z);
    }

    @Override
    public void encode() {
        this.reset();
        this.putBlockVector3(this.x, this.y, this.z);
    }
}
