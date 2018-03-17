package cn.nukkit.network.protocol;

import cn.nukkit.Player;

public class ResourcePackDataInfoPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.RESOURCE_PACK_DATA_INFO_PACKET;

    public String packId;
    public int maxChunkSize;
    public int chunkCount;
    public long compressedPackSize;
    public byte[] sha256;

    @Override
    public void decode() {
        this.packId = this.getString();
        this.maxChunkSize = this.getLInt();
        this.chunkCount = this.getLInt();
        this.compressedPackSize = this.getLLong();
        this.sha256 = this.getByteArray();
    }

    @Override
    public void encode() {
        this.reset();
        this.putString(this.packId);
        this.putLInt(this.maxChunkSize);
        this.putLInt(this.chunkCount);
        this.putLLong(this.compressedPackSize);
        this.putByteArray(this.sha256);
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    protected void handle(Player player) {
        player.handle(this);
    }
}
