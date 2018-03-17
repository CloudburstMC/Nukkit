package cn.nukkit.network.protocol;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.lang.TranslationContainer;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class SetPlayerGameTypePacket extends DataPacket {
    public final static byte NETWORK_ID = ProtocolInfo.SET_PLAYER_GAME_TYPE_PACKET;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    public int gamemode;

    @Override
    public void decode() {
        this.gamemode = this.getVarInt();
    }

    @Override
    public void encode() {
        this.reset();
        this.putVarInt(this.gamemode);
    }

    @Override
    protected void handle(Player player) {
        player.handle(this);
    }
}
