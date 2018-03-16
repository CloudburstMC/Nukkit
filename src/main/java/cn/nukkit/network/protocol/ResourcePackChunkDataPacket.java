package cn.nukkit.network.protocol;

import cn.nukkit.Player;

public class ResourcePackChunkDataPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.RESOURCE_PACK_CHUNK_DATA_PACKET;

    public String packId;
    public int chunkIndex;
    public long progress;
    public byte[] data;

    @Override
    public void decode() {
        this.packId = this.getString();
        this.chunkIndex = this.getLInt();
        this.progress = this.getLLong();
        this.data = this.get(this.getLInt());
    }

    @Override
    public void encode() {
        this.reset();
        this.putString(this.packId);
        this.putLInt(this.chunkIndex);
        this.putLLong(this.progress);
        this.putLInt(this.data.length);
        this.put(this.data);
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    protected void handle(Player player) {

    }
}
