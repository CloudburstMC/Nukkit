package cn.nukkit.network.protocol;

import io.netty.buffer.ByteBuf;
import lombok.ToString;

@ToString
public class NPCRequestPacket extends DataPacket {

    @Override
    public short pid() {
        return ProtocolInfo.NPC_REQUEST_PACKET;
    }

    @Override
    protected void decode(ByteBuf buffer) {

    }

    @Override
    protected void encode(ByteBuf buffer) {
        //TODO
    }
}
