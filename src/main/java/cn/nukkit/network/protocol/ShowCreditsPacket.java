package cn.nukkit.network.protocol;

import cn.nukkit.utils.Binary;
import io.netty.buffer.ByteBuf;
import lombok.ToString;

@ToString
public class ShowCreditsPacket extends DataPacket {

    public static final short NETWORK_ID = ProtocolInfo.SHOW_CREDITS_PACKET;

    public static final int STATUS_START_CREDITS = 0;
    public static final int STATUS_END_CREDITS = 1;

    public long eid;
    public int status;

    @Override
    public short pid() {
        return NETWORK_ID;
    }

    @Override
    protected void decode(ByteBuf buffer) {
        this.eid = Binary.readEntityRuntimeId(buffer);
        this.status = Binary.readVarInt(buffer);
    }

    @Override
    protected void encode(ByteBuf buffer) {
        Binary.writeEntityRuntimeId(buffer, this.eid);
        Binary.writeVarInt(buffer, this.status);
    }
}
