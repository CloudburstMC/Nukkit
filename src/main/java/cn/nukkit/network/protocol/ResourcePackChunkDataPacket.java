package cn.nukkit.network.protocol;

import lombok.ToString;

import java.util.UUID;

@ToString(exclude = "data")
public class ResourcePackChunkDataPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.RESOURCE_PACK_CHUNK_DATA_PACKET;

    public UUID packId;
    public int chunkIndex;
    public long progress;
    public byte[] data;

    @Override
    public void decode() {
        this.packId = UUID.fromString(this.getString());
        this.chunkIndex = this.getLInt();
        this.progress = this.getLLong();
        this.data = this.getByteArray();
    }

    @Override
    public void encode() {
        this.reset();
        this.putString(this.packId.toString());
        this.putLInt(this.chunkIndex);
        this.putLLong(this.progress);
        this.putByteArray(this.data);
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }
}
