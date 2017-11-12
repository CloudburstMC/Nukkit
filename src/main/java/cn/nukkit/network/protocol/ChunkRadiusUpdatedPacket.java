package cn.nukkit.network.protocol;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ChunkRadiusUpdatedPacket extends DataPacket {

    public int radius;

    @Override
    public void decode(PlayerProtocol protocol) {
        this.radius = this.getVarInt();
    }

    @Override
    public void encode(PlayerProtocol protocol) {
        super.reset(protocol);
        this.putVarInt(this.radius);
    }

    @Override
    public byte pid(PlayerProtocol protocol) {
        return protocol.equals(PlayerProtocol.PLAYER_PROTOCOL_113) ?
                ProtocolInfo113.CHUNK_RADIUS_UPDATED_PACKET :
                ProtocolInfo.CHUNK_RADIUS_UPDATED_PACKET;
    }

}
