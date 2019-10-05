package cn.nukkit.network.protocol;

import cn.nukkit.utils.Binary;
import io.netty.buffer.ByteBuf;
import lombok.ToString;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@ToString
public class ChunkRadiusUpdatedPacket extends DataPacket {

    public static final short NETWORK_ID = ProtocolInfo.CHUNK_RADIUS_UPDATED_PACKET;

    public int radius;

    @Override
    protected void decode(ByteBuf buffer) {
        this.radius = Binary.readVarInt(buffer);
    }

    @Override
    protected void encode(ByteBuf buffer) {
        Binary.writeVarInt(buffer, this.radius);
    }

    @Override
    public short pid() {
        return NETWORK_ID;
    }

}
