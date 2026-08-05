package cn.nukkit.network.protocol;

import lombok.ToString;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
@ToString(exclude = "data")
public class LevelChunkPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.FULL_CHUNK_DATA_PACKET;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    private static final long[] EMPTY_long = new long[0];

    public int chunkX;
    public int chunkZ;
    public int dimension;
    public int subChunkCount;
    public boolean cacheEnabled;
    public boolean requestSubChunks;
    public int subChunkLimit;
    public long[] blobIds = EMPTY_long;
    public byte[] data;

    @Override
    public void decode() {
        this.decodeUnsupported();
    }

    @Override
    public void encode() {
        this.reset();

        this.putVarInt(this.chunkX);
        this.putVarInt(this.chunkZ);
        this.putVarInt(this.dimension);

        this.putUnsignedVarInt(this.subChunkCount);

        if (this.requestSubChunks) {
            this.putBoolean(true);
            this.putVarInt(this.subChunkLimit);
        } else {
            this.putBoolean(false);
        }

        this.putBoolean(cacheEnabled);

        this.putUnsignedVarInt(blobIds.length);
        for (long blobId : blobIds) {
            this.putLLong(blobId);
        }
        this.putByteArray(this.data);
    }
}
