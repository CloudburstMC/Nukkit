package cn.nukkit.network.protocol;

import cn.nukkit.utils.Binary;
import io.netty.buffer.ByteBuf;
import lombok.ToString;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@ToString
public class ShowProfilePacket extends DataPacket {
    public static final short NETWORK_ID = ProtocolInfo.SHOW_PROFILE_PACKET;

    public String xuid;

    @Override
    public short pid() {
        return NETWORK_ID;
    }

    @Override
    protected void decode(ByteBuf buffer) {
        this.xuid = Binary.readString(buffer);
    }

    @Override
    protected void encode(ByteBuf buffer) {
        Binary.writeString(buffer, this.xuid);
    }

}
