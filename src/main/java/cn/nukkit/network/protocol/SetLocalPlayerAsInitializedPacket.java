package cn.nukkit.network.protocol;

import cn.nukkit.utils.Binary;
import io.netty.buffer.ByteBuf;
import lombok.ToString;

@ToString
public class SetLocalPlayerAsInitializedPacket extends DataPacket {
    public static final short NETWORK_ID = ProtocolInfo.SET_LOCAL_PLAYER_AS_INITIALIZED_PACKET;

    public long eid;

    @Override
    public short pid() {
        return NETWORK_ID;
    }

    @Override
    protected void decode(ByteBuf buffer) {
        eid = Binary.readUnsignedVarLong(buffer);
    }

    @Override
    protected void encode(ByteBuf buffer) {
        Binary.writeUnsignedVarLong(buffer, eid);
    }
}
