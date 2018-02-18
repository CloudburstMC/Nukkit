package cn.nukkit.network.protocol;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class FullChunkDataPacket extends DataPacket {

    @Override
    public byte pid(PlayerProtocol protocol) {
        return protocol.getPacketId("FULL_CHUNK_DATA_PACKET");
    }

    public int chunkX;
    public int chunkZ;
    public byte[] data;

    @Override
    public void decode(PlayerProtocol protocol) {

    }

    @Override
    public void encode(PlayerProtocol protocol) {
        this.reset(protocol);
        this.putVarInt(this.chunkX);
        this.putVarInt(this.chunkZ);
        this.putByteArray(this.data);
    }
}
