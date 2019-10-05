package cn.nukkit.network.protocol;

import cn.nukkit.utils.Binary;
import io.netty.buffer.ByteBuf;
import lombok.ToString;

import java.util.UUID;

@ToString
public class ResourcePackChunkRequestPacket extends DataPacket {

    public static final short NETWORK_ID = ProtocolInfo.RESOURCE_PACK_CHUNK_REQUEST_PACKET;

    public UUID packId;
    public int chunkIndex;

    @Override
    protected void decode(ByteBuf buffer) {
        this.packId = UUID.fromString(Binary.readString(buffer));
        this.chunkIndex = buffer.readIntLE();
    }

    @Override
    protected void encode(ByteBuf buffer) {
        Binary.writeString(buffer, this.packId.toString());
        buffer.writeIntLE(this.chunkIndex);
    }

    @Override
    public short pid() {
        return NETWORK_ID;
    }
}
