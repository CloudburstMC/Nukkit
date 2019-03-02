package cn.nukkit.network.protocol;

import cn.nukkit.math.BlockVector3;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockEventPacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.BLOCK_EVENT_PACKET;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    public int x;
    public int y;
    public int z;
    public int case1;
    public int case2;

    @Override
    public void decode() {
        BlockVector3 v = this.getBlockVector3();
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        this.case1 = this.getVarInt();
        this.case2 = this.getVarInt();
    }

    @Override
    public void encode() {
        this.reset();
        this.putBlockVector3(this.x, this.y, this.z);
        this.putVarInt(this.case1);
        this.putVarInt(this.case2);
    }
}
