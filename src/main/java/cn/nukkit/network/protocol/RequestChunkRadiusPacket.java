package cn.nukkit.network.protocol;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class RequestChunkRadiusPacket extends DataPacket {

    public int radius;

    @Override
    public void decode(PlayerProtocol protocol) {
        this.radius = this.getVarInt();
    }

    @Override
    public void encode(PlayerProtocol protocol) {

    }

    @Override
    public byte pid(PlayerProtocol protocol) {
        return protocol.equals(PlayerProtocol.PLAYER_PROTOCOL_113) ?
                ProtocolInfo113.REQUEST_CHUNK_RADIUS_PACKET :
                ProtocolInfo.REQUEST_CHUNK_RADIUS_PACKET;
    }

}
