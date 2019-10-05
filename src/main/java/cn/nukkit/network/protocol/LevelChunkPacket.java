package cn.nukkit.network.protocol;

import cn.nukkit.utils.Binary;
import io.netty.buffer.ByteBuf;
import io.netty.util.ReferenceCounted;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@Log4j2
@ToString(exclude = "data")
public class LevelChunkPacket extends DataPacket implements ReferenceCounted {
    public static final short NETWORK_ID = ProtocolInfo.FULL_CHUNK_DATA_PACKET;
    public ByteBuf data;

    public int chunkX;
    public int chunkZ;
    public int subChunkCount;
    public boolean cacheEnabled;
    public long[] blobIds;

    @Override
    public short pid() {
        return NETWORK_ID;
    }

    @Override
    protected void decode(ByteBuf buffer) {

    }

    @Override
    protected void encode(ByteBuf buffer) {
        Binary.writeVarInt(buffer, this.chunkX);
        Binary.writeVarInt(buffer, this.chunkZ);
        Binary.writeUnsignedVarInt(buffer, this.subChunkCount);
        buffer.writeBoolean(cacheEnabled);
        if (this.cacheEnabled) {
            Binary.writeUnsignedVarInt(buffer, blobIds.length);

            for (long blobId : blobIds) {
                buffer.writeLongLE(blobId);
            }
        }
        Binary.writeVarIntBuffer(buffer, this.data);
    }

    @Override
    public int refCnt() {
        return this.data.refCnt();
    }

    @Override
    public LevelChunkPacket retain() {
        this.data.retain();
        return this;
    }

    @Override
    public LevelChunkPacket retain(int increment) {
        this.data.retain(increment);
        return this;
    }

    @Override
    public LevelChunkPacket touch() {
        this.data.touch();
        return this;
    }

    @Override
    public LevelChunkPacket touch(Object hint) {
        this.data.touch(hint);
        return this;
    }

    @Override
    public boolean release() {
        return this.data.release();
    }

    @Override
    public boolean release(int decrement) {
        return this.data.release(decrement);
    }

    public LevelChunkPacket copy() {
        LevelChunkPacket packet = new LevelChunkPacket();
        packet.data = this.data.retainedSlice();
        packet.chunkX = this.chunkX;
        packet.chunkZ = this.chunkZ;
        packet.subChunkCount = this.subChunkCount;
        packet.blobIds = this.blobIds;
        packet.cacheEnabled = this.cacheEnabled;

        return packet;
    }
}
