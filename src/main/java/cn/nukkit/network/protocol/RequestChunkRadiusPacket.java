package cn.nukkit.network.protocol;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class RequestChunkRadiusPacket extends DataPacket {

    public int radius;

    @Override
    public byte pid(PlayerProtocol protocol) {
        return protocol.getPacketId("REQUEST_CHUNK_RADIUS_PACKET");
    }

    @Override
    public void decode(PlayerProtocol protocol) {
        this.radius = this.getVarInt();
    }

    @Override
    public void encode(PlayerProtocol protocol) {

    }

}
