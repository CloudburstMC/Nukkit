package cn.nukkit.network.protocol;

import lombok.ToString;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
@ToString
public class ChunkRadiusUpdatedPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.CHUNK_RADIUS_UPDATED_PACKET;

    public int radius;

    @Override
    public void decode() {
        this.decodeUnsupported();
    }

    @Override
    public void encode() {
        this.reset();
        this.putVarInt(this.radius);
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }
}
