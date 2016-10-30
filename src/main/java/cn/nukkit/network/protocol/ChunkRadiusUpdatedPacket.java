package cn.nukkit.network.protocol;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ChunkRadiusUpdatedPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.CHUNK_RADIUS_UPDATED_PACKET;

    public long radius;

    @Override
    public void decode() {
        this.radius = this.getUnsignedVarInt();
    }

    @Override
    public void encode() {
        super.reset();
        this.putUnsignedVarInt(this.radius);
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

}
