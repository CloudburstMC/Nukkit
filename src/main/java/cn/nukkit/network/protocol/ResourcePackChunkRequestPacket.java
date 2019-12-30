package cn.nukkit.network.protocol;

import cn.nukkit.utils.Binary;
import io.netty.buffer.ByteBuf;
import lombok.ToString;

@ToString
public class ResourcePackChunkRequestPacket extends DataPacket {

    public static final short NETWORK_ID = ProtocolInfo.RESOURCE_PACK_CHUNK_REQUEST_PACKET;

    public String packId;
    public int chunkIndex;

    @Override
    protected void decode(ByteBuf buffer) {
        this.packId = Binary.readString(buffer);
        this.chunkIndex = buffer.readIntLE();
    }

    @Override
    protected void encode(ByteBuf buffer) {
        Binary.writeString(buffer, this.packId);
        buffer.writeIntLE(this.chunkIndex);
    }

    @Override
    public short pid() {
        return NETWORK_ID;
    }
}
