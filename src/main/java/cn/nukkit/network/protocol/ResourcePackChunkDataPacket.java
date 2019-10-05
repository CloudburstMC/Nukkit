package cn.nukkit.network.protocol;

import cn.nukkit.utils.Binary;
import io.netty.buffer.ByteBuf;
import lombok.ToString;

import java.util.UUID;

@ToString(exclude = "data")
public class ResourcePackChunkDataPacket extends DataPacket {

    public static final short NETWORK_ID = ProtocolInfo.RESOURCE_PACK_CHUNK_DATA_PACKET;

    public UUID packId;
    public int chunkIndex;
    public long progress;
    public byte[] data;

    @Override
    protected void decode(ByteBuf buffer) {
        this.packId = UUID.fromString(Binary.readString(buffer));
        this.chunkIndex = buffer.readIntLE();
        this.progress = buffer.readLongLE();
        this.data = new byte[buffer.readIntLE()];
        buffer.readBytes(this.data);
    }

    @Override
    protected void encode(ByteBuf buffer) {
        Binary.writeString(buffer, this.packId.toString());
        buffer.writeIntLE(this.chunkIndex);
        buffer.writeLongLE(this.progress);
        buffer.writeIntLE(this.data.length);
        buffer.writeBytes(this.data);
    }

    @Override
    public short pid() {
        return NETWORK_ID;
    }
}
