package cn.nukkit.network.protocol;

public class ResourcePackChunkDataPacket extends DataPacket {

    public String packId;
    public int chunkIndex;
    public long progress;
    public byte[] data;

    @Override
    public void decode(PlayerProtocol protocol) {
        this.packId = this.getString();
        this.chunkIndex = this.getLInt();
        this.progress = this.getLLong();
        this.data = this.get(this.getLInt());
    }

    @Override
    public void encode(PlayerProtocol protocol) {
        this.reset(protocol);
        this.putString(this.packId);
        this.putLInt(this.chunkIndex);
        this.putLLong(this.progress);
        this.putLInt(this.data.length);
        this.put(this.data);
    }

    @Override
    public byte pid(PlayerProtocol protocol) {
        return protocol.equals(PlayerProtocol.PLAYER_PROTOCOL_113) ?
                ProtocolInfo113.RESOURCE_PACK_CHUNK_DATA_PACKET :
                ProtocolInfo.RESOURCE_PACK_CHUNK_DATA_PACKET;
    }
}
