package cn.nukkit.network.protocol;

import cn.nukkit.level.GameRules;
import lombok.ToString;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@ToString
public class GameRulesChangedPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.GAME_RULES_CHANGED_PACKET;

    public GameRules gameRules;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        //TODO: this.gameRules = this.getGameRules();
    }

    @Override
    public void encode() {
        this.reset();
        this.putGameRules(this.gameRules);
    }
}
