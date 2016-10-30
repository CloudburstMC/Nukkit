package cn.nukkit.network.protocol;

import cn.nukkit.math.BlockVector3;

/**
 * @author Nukkit Project Team
 */
public class RemoveBlockPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.REMOVE_BLOCK_PACKET;

    public int x;
    public int y;
    public int z;

    @Override
    public void decode() {
        BlockVector3 v = this.getBlockCoords();
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
    }

    @Override
    public void encode() {
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

}
