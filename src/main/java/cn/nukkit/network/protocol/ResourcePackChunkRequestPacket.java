package cn.nukkit.network.protocol;

public class ResourcePackChunkRequestPacket extends DataPacket {

    public String packId;
    public int chunkIndex;

    @Override
    public byte pid(PlayerProtocol protocol) {
        return protocol.getPacketId("RESOURCE_PACK_CHUNK_REQUEST_PACKET");
    }

    @Override
    public void decode(PlayerProtocol protocol) {
        this.packId = this.getString();
        this.chunkIndex = this.getLInt();
    }

    @Override
    public void encode(PlayerProtocol protocol) {
        this.reset(protocol);
        this.putString(this.packId);
        this.putLInt(this.chunkIndex);
    }

}
