package cn.nukkit.network.protocol;

import cn.nukkit.math.BlockVector3;
import cn.nukkit.utils.Binary;
import io.netty.buffer.ByteBuf;
import lombok.ToString;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@ToString(exclude = "namedTag")
public class BlockEntityDataPacket extends DataPacket {
    public static final short NETWORK_ID = ProtocolInfo.BLOCK_ENTITY_DATA_PACKET;

    public int x;
    public int y;
    public int z;
    public byte[] namedTag;

    @Override
    public short pid() {
        return NETWORK_ID;
    }

    @Override
    protected void decode(ByteBuf buffer) {
        BlockVector3 v = Binary.readBlockVector3(buffer);
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        this.namedTag = new byte[buffer.readableBytes()];
        buffer.readBytes(this.namedTag);
    }

    @Override
    protected void encode(ByteBuf buffer) {
        Binary.writeBlockVector3(buffer, this.x, this.y, this.z);
        buffer.writeBytes(this.namedTag);
    }
}