package cn.nukkit.network.protocol;

public class ResourcePackChunkRequestPacket extends DataPacket {

    public String packId;
    public int chunkIndex;

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

    @Override
    public byte pid(PlayerProtocol protocol) {
        return protocol.equals(PlayerProtocol.PLAYER_PROTOCOL_113) ?
                ProtocolInfo113.RESOURCE_PACK_CHUNK_REQUEST_PACKET :
                ProtocolInfo.RESOURCE_PACK_CHUNK_REQUEST_PACKET;
    }
}
