package cn.nukkit.network.protocol;

import cn.nukkit.utils.Binary;
import io.netty.buffer.ByteBuf;
import lombok.ToString;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@ToString
public class BlockEventPacket extends DataPacket {
    public static final short NETWORK_ID = ProtocolInfo.BLOCK_EVENT_PACKET;

    @Override
    public short pid() {
        return NETWORK_ID;
    }

    public int x;
    public int y;
    public int z;
    public int case1;
    public int case2;

    @Override
    protected void decode(ByteBuf buffer) {

    }

    @Override
    protected void encode(ByteBuf buffer) {
        Binary.writeBlockVector3(buffer, this.x, this.y, this.z);
        Binary.writeVarInt(buffer, this.case1);
        Binary.writeVarInt(buffer, this.case2);
    }
}
