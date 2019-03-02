package cn.nukkit.network.protocol;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class RequestChunkRadiusPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.REQUEST_CHUNK_RADIUS_PACKET;

    public int radius;

    @Override
    public void decode() {
        this.radius = this.getVarInt();
    }

    @Override
    public void encode() {
        this.putVarInt(this.radius);
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

}
