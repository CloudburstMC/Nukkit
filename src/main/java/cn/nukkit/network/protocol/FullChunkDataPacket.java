package cn.nukkit.network.protocol;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class FullChunkDataPacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.FULL_CHUNK_DATA_PACKET;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    public int chunkX;
    public int chunkZ;
    public byte[] data;

    @Override
    public void decode() {
        this.chunkX = this.getVarInt();
        this.chunkZ = this.getVarInt();
        this.data = this.getByteArray();
    }

    @Override
    public void encode() {
        this.reset();
        this.putVarInt(this.chunkX);
        this.putVarInt(this.chunkZ);
        this.putByteArray(this.data);
    }
}
