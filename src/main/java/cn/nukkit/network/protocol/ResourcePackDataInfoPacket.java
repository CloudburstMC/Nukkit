package cn.nukkit.network.protocol;

public class ResourcePackDataInfoPacket extends DataPacket {

    public String packId;
    public int maxChunkSize;
    public int chunkCount;
    public long compressedPackSize;
    public byte[] sha256;

    @Override
    public byte pid(PlayerProtocol protocol) {
        return protocol.getPacketId("RESOURCE_PACK_DATA_INFO_PACKET");
    }

    @Override
    public void decode(PlayerProtocol protocol) {
        this.packId = this.getString();
        this.maxChunkSize = this.getLInt();
        this.chunkCount = this.getLInt();
        this.compressedPackSize = this.getLLong();
        this.sha256 = this.getByteArray();
    }

    @Override
    public void encode(PlayerProtocol protocol) {
        this.reset(protocol);
        this.putString(this.packId);
        this.putLInt(this.maxChunkSize);
        this.putLInt(this.chunkCount);
        this.putLLong(this.compressedPackSize);
        this.putByteArray(this.sha256);
    }

}
