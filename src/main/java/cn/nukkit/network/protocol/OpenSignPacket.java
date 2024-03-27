package cn.nukkit.network.protocol;

import cn.nukkit.math.BlockVector3;

public class OpenSignPacket extends DataPacket {

    public BlockVector3 position;
    public boolean frontSide;

    @Override
    public byte pid() {
        return ProtocolInfo.__INTERNAL__OPEN_SIGN_PACKET;
    }

    @Override
    public void decode() {
        this.decodeUnsupported();
    }

    @Override
    public void encode() {
        this.reset();
        this.putBlockVector3(this.position);
        this.putBoolean(this.frontSide);
    }
}
