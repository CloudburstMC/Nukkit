package cn.nukkit.network.protocol;

import cn.nukkit.math.BlockVector3;
import cn.nukkit.utils.Binary;
import io.netty.buffer.ByteBuf;
import lombok.ToString;

@ToString
public class NetworkChunkPublisherUpdatePacket extends DataPacket {

    public BlockVector3 position;
    public int radius;

    @Override
    public short pid() {
        return ProtocolInfo.NETWORK_CHUNK_PUBLISHER_UPDATE_PACKET;
    }

    @Override
    protected void decode(ByteBuf buffer) {
        this.position = Binary.readSignedBlockPosition(buffer);
        this.radius = (int) Binary.readUnsignedVarInt(buffer);
    }

    @Override
    protected void encode(ByteBuf buffer) {
        Binary.writeSignedBlockPosition(buffer, position);
        Binary.writeUnsignedVarInt(buffer, radius);
    }
}
