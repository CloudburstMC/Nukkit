package cn.nukkit.network.protocol;

import cn.nukkit.math.BlockVector3;
import cn.nukkit.utils.Binary;
import io.netty.buffer.ByteBuf;
import lombok.ToString;

@ToString
public class BlockPickRequestPacket extends DataPacket {

    public static final short NETWORK_ID = ProtocolInfo.BLOCK_PICK_REQUEST_PACKET;

    public int x;
    public int y;
    public int z;
    public boolean addUserData;
    public int selectedSlot;

    @Override
    public short pid() {
        return NETWORK_ID;
    }

    @Override
    protected void decode(ByteBuf buffer) {
        BlockVector3 v = Binary.readSignedBlockPosition(buffer);
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        this.addUserData = buffer.readBoolean();
        this.selectedSlot = buffer.readUnsignedByte();
    }

    @Override
    protected void encode(ByteBuf buffer) {

    }
}
