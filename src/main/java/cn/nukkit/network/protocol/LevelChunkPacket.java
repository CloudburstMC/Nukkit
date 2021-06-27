package cn.nukkit.network.protocol;

import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@ToString(exclude = "data")
public class LevelChunkPacket extends DataPacket {

    public int chunkX;
    public int chunkZ;
    public int subChunkCount;
    public boolean cacheEnabled;
    public List<Long> blobIds = new ArrayList<>();
    public String data;

    @Override
    public byte pid() {
        return ProtocolInfo.LEVEL_CHUNK_PACKET;
    }

    @Override
    public void decode() {
        this.chunkX = this.getVarInt();
        this.chunkZ = this.getVarInt();
        this.subChunkCount = (int) this.getUnsignedVarInt();
        this.cacheEnabled = this.getBoolean();
        if (this.cacheEnabled) {
            for (int i =  0, count = (int) this.getUnsignedVarInt(); i < count; i++) {
                this.blobIds.add(this.getLLong());
            }
        }
        this.data = this.getString();
    }

    @Override
    public void encode() {
        this.reset();
        this.putVarInt(this.chunkX);
        this.putVarInt(this.chunkZ);
        this.putUnsignedVarInt(this.subChunkCount);
        this.putBoolean(this.cacheEnabled);
        if (this.cacheEnabled) {
            this.putUnsignedVarInt(this.blobIds.size());
            for (long blobId : this.blobIds) {
                this.putLLong(blobId);
            }
        }
        this.putString(this.data);
    }
}
