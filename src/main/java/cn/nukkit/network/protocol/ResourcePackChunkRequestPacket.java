package cn.nukkit.network.protocol;

import cn.nukkit.Player;
import cn.nukkit.resourcepacks.ResourcePack;

public class ResourcePackChunkRequestPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.RESOURCE_PACK_CHUNK_REQUEST_PACKET;

    public String packId;
    public int chunkIndex;

    @Override
    public void decode() {
        this.packId = this.getString();
        this.chunkIndex = this.getLInt();
    }

    @Override
    public void encode() {
        this.reset();
        this.putString(this.packId);
        this.putLInt(this.chunkIndex);
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void handle(Player player) {
        ResourcePack resourcePack = player.server.getResourcePackManager().getPackById(this.packId);
        if (resourcePack == null) {
            player.close("", "disconnectionScreen.resourcePack");
            return;
        }

        ResourcePackChunkDataPacket dataPacket = new ResourcePackChunkDataPacket();
        dataPacket.packId = resourcePack.getPackId();
        dataPacket.chunkIndex = this.chunkIndex;
        dataPacket.data = resourcePack.getPackChunk(1048576 * this.chunkIndex, 1048576);
        dataPacket.progress = 1048576 * this.chunkIndex;
        player.dataPacket(dataPacket);
    }
}
