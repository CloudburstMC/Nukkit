package cn.nukkit.network.protocol;

import cn.nukkit.math.BlockVector3;
import cn.nukkit.utils.Binary;
import io.netty.buffer.ByteBuf;
import lombok.ToString;

/**
 * Created by Pub4Game on 03.07.2016.
 */
@ToString
public class ItemFrameDropItemPacket extends DataPacket {

    public static final short NETWORK_ID = ProtocolInfo.ITEM_FRAME_DROP_ITEM_PACKET;

    public int x;
    public int y;
    public int z;

    @Override
    protected void decode(ByteBuf buffer) {
        BlockVector3 v = Binary.readBlockVector3(buffer);
        this.z = v.z;
        this.y = v.y;
        this.x = v.x;
    }

    @Override
    protected void encode(ByteBuf buffer) {

    }

    @Override
    public short pid() {
        return NETWORK_ID;
    }
}
