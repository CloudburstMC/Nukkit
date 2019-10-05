package cn.nukkit.network.protocol;

import io.netty.buffer.ByteBuf;
import lombok.ToString;

@ToString
public class EntityPickRequestPacket extends DataPacket {

    public static final short NETWORK_ID = ProtocolInfo.ENTITY_PICK_REQUEST_PACKET;

    @Override
    public short pid() {
        return NETWORK_ID;
    }

    @Override
    protected void decode(ByteBuf buffer) {

    }

    @Override
    protected void encode(ByteBuf buffer) {
        //TODO
    }
}
