package cn.nukkit.network.protocol;

import cn.nukkit.utils.Binary;
import io.netty.buffer.ByteBuf;
import lombok.ToString;

/**
 * Created on 2016/1/5 by xtypr.
 * Package cn.nukkit.network.protocol in project nukkit .
 */
@ToString
public class ChangeDimensionPacket extends DataPacket {

    public static final short NETWORK_ID = ProtocolInfo.CHANGE_DIMENSION_PACKET;

    public int dimension;

    public float x;
    public float y;
    public float z;

    public boolean respawn;

    @Override
    protected void decode(ByteBuf buffer) {

    }

    @Override
    protected void encode(ByteBuf buffer) {
        Binary.writeVarInt(buffer, this.dimension);
        Binary.writeVector3f(buffer, this.x, this.y, this.z);
        buffer.writeBoolean(this.respawn);
    }

    @Override
    public short pid() {
        return NETWORK_ID;
    }
}
