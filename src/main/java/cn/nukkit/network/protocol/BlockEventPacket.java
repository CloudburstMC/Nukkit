package cn.nukkit.network.protocol;

import cn.nukkit.math.BlockVector3;
import lombok.ToString;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@ToString
public class BlockEventPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.BLOCK_EVENT_PACKET;

    public int x;
    public int y;
    public int z;
    public int eventType = 1;
    public int eventData;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        BlockVector3 blockVector3 = this.getBlockVector3();
        this.x = blockVector3.getX();
        this.y = blockVector3.getY();
        this.z = blockVector3.getZ();
        this.eventType = this.getVarInt();
        this.eventData = this.getVarInt();
    }

    @Override
    public void encode() {
        this.reset();
        this.putBlockVector3(this.x, this.y, this.z);
        this.putVarInt(this.eventType);
        this.putVarInt(this.eventData);
    }
}
