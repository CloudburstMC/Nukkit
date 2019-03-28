package cn.nukkit.network.protocol;

import cn.nukkit.math.BlockVector3;
import lombok.ToString;

@ToString
public class LecternUpdatePacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.LECTERN_UPDATE_PACKET;

    public int page;
    public BlockVector3 blockPosition;
    public boolean unknownBool;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        page = this.getByte();
        blockPosition = this.getBlockVector3();
        unknownBool = this.getBoolean();
    }

    @Override
    public void encode() {
    }
}
