package cn.nukkit.network.protocol;

import cn.nukkit.math.BlockVector3;

public class BlockPickRequestPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.BLOCK_PICK_REQUEST_PACKET;

    public int x;
    public int y;
    public int z;
    public int selectedSlot;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        BlockVector3 v = this.getBlockCoords();
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        this.selectedSlot = this.getByte();
    }

    @Override
    public void encode() {

    }
}
