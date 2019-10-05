package cn.nukkit.network.protocol;

import io.netty.buffer.ByteBuf;
import lombok.ToString;

@ToString
public class SetLastHurtByPacket extends DataPacket {

    @Override
    public short pid() {
        return ProtocolInfo.SET_LAST_HURT_BY_PACKET;
    }

    @Override
    protected void decode(ByteBuf buffer) {

    }

    @Override
    protected void encode(ByteBuf buffer) {
        //TODO
    }
}
