package cn.nukkit.network.protocol;

import lombok.ToString;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@ToString
public class ChunkRadiusUpdatedPacket extends DataPacket {

    public int radius;

    @Override
    public byte pid() {
        return ProtocolInfo.CHUNK_RADIUS_UPDATED_PACKET;
    }

    @Override
    public void decode() {
        this.radius = this.getVarInt();
    }

    @Override
    public void encode() {
        super.reset();
        this.putVarInt(this.radius);
    }
}
