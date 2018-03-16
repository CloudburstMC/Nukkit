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
    public void handle(Player player) {
        if (this.gamemode != player.gamemode) {
            if (!player.hasPermission("nukkit.command.gamemode")) {
                SetPlayerGameTypePacket setPlayerGameTypePacket1 = new SetPlayerGameTypePacket();
                setPlayerGameTypePacket1.gamemode = player.gamemode & 0x01;
                player.dataPacket(setPlayerGameTypePacket1);
                player.getAdventureSettings().update();
                return;
            }
            player.setGamemode(this.gamemode, true);
            Command.broadcastCommandMessage(player, new TranslationContainer("commands.gamemode.success.self", Server.getGamemodeString(player.gamemode)));
        }
    }
}
