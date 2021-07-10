package cn.nukkit.network.protocol;

import lombok.ToString;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@ToString(exclude = "data")
public class LevelChunkPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.FULL_CHUNK_DATA_PACKET;

    public int chunkX;
    public int chunkZ;
    public int subChunkCount;
    public boolean cacheEnabled;
    public long[] blobIds = new long[0];
    public byte[] data;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.chunkX = this.getVarInt();
        this.chunkZ = this.getVarInt();
        this.subChunkCount = (int) this.getUnsignedVarInt();
        this.cacheEnabled = this.getBoolean();
        if (this.cacheEnabled) {
            int count = (int) this.getUnsignedVarInt();
            this.blobIds = new long[count];
            for (int i = 0; i < count; i++) {
                this.blobIds[i] = this.getLLong();
            }
        }
        this.data = this.getByteArray();
    }

    @Override
    public void encode() {
        this.reset();
        this.putVarInt(this.chunkX);
        this.putVarInt(this.chunkZ);
        this.putUnsignedVarInt(this.subChunkCount);
        this.putBoolean(this.cacheEnabled);
        if (this.cacheEnabled) {
            this.putUnsignedVarInt(this.blobIds.length);
            for (long blobId : this.blobIds) {
                this.putLLong(blobId);
            }
        }
        this.putByteArray(this.data);
    }
}
