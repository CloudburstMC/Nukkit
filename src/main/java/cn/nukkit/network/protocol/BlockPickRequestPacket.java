package cn.nukkit.network.protocol;

import cn.nukkit.math.BlockVector3;

public class BlockPickRequestPacket extends DataPacket {

    public int x;
    public int y;
    public int z;
    public boolean addUserData;
    public int selectedSlot;

    @Override
    public byte pid(PlayerProtocol protocol) {
        return protocol.getPacketId("BLOCK_PICK_REQUEST_PACKET");
    }

    @Override
    public void decode(PlayerProtocol protocol) {
        BlockVector3 v = this.getSignedBlockPosition();
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        this.putBoolean(this.addUserData);
        this.selectedSlot = this.getByte();
    }

    @Override
    public void encode(PlayerProtocol protocol) {

    }
}
