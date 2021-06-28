package cn.nukkit.network.protocol;

import cn.nukkit.level.GameRules;
import lombok.ToString;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@ToString
public class GameRulesChangedPacket extends DataPacket {

    public GameRules gameRules;

    @Override
    public byte pid() {
        return ProtocolInfo.GAME_RULES_CHANGED_PACKET;
    }

    @Override
    public void decode() {
        this.gameRules = this.getGameRules();
    }

    @Override
    public void encode() {
        this.reset();
        this.putGameRules(this.gameRules);
    }
}
