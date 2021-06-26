package cn.nukkit.network.protocol;

import lombok.ToString;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@ToString
public class SetPlayerGameTypePacket extends DataPacket {

    public int gamemode;

    @Override
    public byte pid() {
        return ProtocolInfo.SET_PLAYER_GAME_TYPE_PACKET;
    }

    @Override
    public void decode() {
        this.gamemode = this.getVarInt();
    }

    @Override
    public void encode() {
        this.reset();
        this.putVarInt(this.gamemode);
    }
}
