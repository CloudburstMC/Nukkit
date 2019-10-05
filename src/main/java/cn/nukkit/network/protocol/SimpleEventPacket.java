package cn.nukkit.network.protocol;

import io.netty.buffer.ByteBuf;
import lombok.ToString;

@ToString
public class SimpleEventPacket extends DataPacket {

    public short unknown;

    @Override
    public short pid() {
        return ProtocolInfo.SIMPLE_EVENT_PACKET;
    }

    @Override
    protected void decode(ByteBuf buffer) {

    }

    @Override
    protected void encode(ByteBuf buffer) {
        buffer.writeShort(this.unknown);
    }
}
