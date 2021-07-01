package cn.nukkit.network.protocol;

import lombok.ToString;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@ToString
public class RequestChunkRadiusPacket extends DataPacket {

    public int radius;

    @Override
    public byte pid() {
        return ProtocolInfo.REQUEST_CHUNK_RADIUS_PACKET;
    }

    @Override
    public void decode() {
        this.radius = this.getVarInt();
    }

    @Override
    public void encode() {
    	this.reset();
        this.putVarInt(this.radius);
    }
}
