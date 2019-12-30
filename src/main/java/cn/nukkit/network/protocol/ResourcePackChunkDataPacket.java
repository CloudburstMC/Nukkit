package cn.nukkit.network.protocol;

import cn.nukkit.utils.Binary;
import io.netty.buffer.ByteBuf;
import lombok.ToString;

@ToString(exclude = "data")
public class ResourcePackChunkDataPacket extends DataPacket {

    public static final short NETWORK_ID = ProtocolInfo.RESOURCE_PACK_CHUNK_DATA_PACKET;

    public String packId;
    public int chunkIndex;
    public long progress;
    public byte[] data;

    @Override
    protected void decode(ByteBuf buffer) {
        this.packId = Binary.readString(buffer);
        this.chunkIndex = buffer.readIntLE();
        this.progress = buffer.readLongLE();
        this.data = Binary.readByteArray(buffer);
    }

    @Override
    protected void encode(ByteBuf buffer) {
        Binary.writeString(buffer, this.packId);
        buffer.writeIntLE(this.chunkIndex);
        buffer.writeLongLE(this.progress);
        Binary.writeByteArray(buffer, this.data);
    }

    @Override
    public short pid() {
        return NETWORK_ID;
    }
}
