package cn.nukkit.network.protocol;

import cn.nukkit.Player;
import cn.nukkit.level.GameRules;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class GameRulesChangedPacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.GAME_RULES_CHANGED_PACKET;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    public GameRules gameRules;

    @Override
    public void decode() {
    }

    @Override
    public void encode() {
        this.reset();
        putGameRules(gameRules);
    }

    @Override
    protected void handle(Player player) {
        player.handle(this);
    }
}
