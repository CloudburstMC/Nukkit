package cn.nukkit.network.protocol;

import cn.nukkit.utils.Binary;
import io.netty.buffer.ByteBuf;
import lombok.ToString;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@ToString
public class SetPlayerGameTypePacket extends DataPacket {
    public final static byte NETWORK_ID = ProtocolInfo.SET_PLAYER_GAME_TYPE_PACKET;

    @Override
    public short pid() {
        return NETWORK_ID;
    }

    public int gamemode;

    @Override
    protected void decode(ByteBuf buffer) {
        this.gamemode = Binary.readVarInt(buffer);
    }

    @Override
    protected void encode(ByteBuf buffer) {
        Binary.writeVarInt(buffer, this.gamemode);
    }
}
