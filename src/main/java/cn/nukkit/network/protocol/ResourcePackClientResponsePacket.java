package cn.nukkit.network.protocol;

import cn.nukkit.Player;
import cn.nukkit.resourcepacks.ResourcePack;

public class ResourcePackClientResponsePacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.RESOURCE_PACK_CLIENT_RESPONSE_PACKET;

    public static final byte STATUS_REFUSED = 1;
    public static final byte STATUS_SEND_PACKS = 2;
    public static final byte STATUS_HAVE_ALL_PACKS = 3;
    public static final byte STATUS_COMPLETED = 4;

    public byte responseStatus;
    public String[] packIds;

    @Override
    public void decode() {
        this.responseStatus = (byte) this.getByte();
        this.packIds = new String[this.getLShort()];
        for (int i = 0; i < this.packIds.length; i++) {
            this.packIds[i] = this.getString();
        }
    }

    @Override
    public void encode() {
        this.reset();
        this.putByte(this.responseStatus);
        this.putLShort(this.packIds.length);
        for (String id : this.packIds) {
            this.putString(id);
        }
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void handle(Player player) {
        switch (this.responseStatus) {
            case ResourcePackClientResponsePacket.STATUS_REFUSED:
                player.close("", "disconnectionScreen.noReason");
                break;
            case ResourcePackClientResponsePacket.STATUS_SEND_PACKS:
                for (String id : this.packIds) {
                    ResourcePack resourcePack = player.server.getResourcePackManager().getPackById(id);
                    if (resourcePack == null) {
                        player.close("", "disconnectionScreen.resourcePack");
                        break;
                    }

                    ResourcePackDataInfoPacket dataInfoPacket = new ResourcePackDataInfoPacket();
                    dataInfoPacket.packId = resourcePack.getPackId();
                    dataInfoPacket.maxChunkSize = 1048576; //megabyte
                    dataInfoPacket.chunkCount = resourcePack.getPackSize() / dataInfoPacket.maxChunkSize;
                    dataInfoPacket.compressedPackSize = resourcePack.getPackSize();
                    dataInfoPacket.sha256 = resourcePack.getSha256();
                    player.dataPacket(dataInfoPacket);
                }
                break;
            case ResourcePackClientResponsePacket.STATUS_HAVE_ALL_PACKS:
                ResourcePackStackPacket stackPacket = new ResourcePackStackPacket();
                stackPacket.mustAccept = player.server.forceResources();
                stackPacket.resourcePackStack = player.server.getResourcePackManager().getResourceStack();
                player.dataPacket(stackPacket);
                break;
            case ResourcePackClientResponsePacket.STATUS_COMPLETED:
                if (player.preLoginEventTask.isFinished()) {
                    player.completeLoginSequence();
                } else {
                    player.shouldLogin = true;
                }
                break;
        }
    }
}
