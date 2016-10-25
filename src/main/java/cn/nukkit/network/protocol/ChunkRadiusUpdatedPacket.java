package cn.nukkit.network.protocol;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ChunkRadiusUpdatedPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.CHUNK_RADIUS_UPDATED_PACKET;

    public int radius;

    @Override
    public void decode() {
    }

    @Override
    public void encode() {
        super.reset();
        this.putInt(this.radius);
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

}
