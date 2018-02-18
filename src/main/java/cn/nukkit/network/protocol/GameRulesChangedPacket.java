package cn.nukkit.network.protocol;

import cn.nukkit.level.GameRules;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class GameRulesChangedPacket extends DataPacket {

    @Override
    public byte pid(PlayerProtocol protocol) {
        return protocol.getPacketId("GAME_RULES_CHANGED_PACKET");
    }

    public GameRules gameRules;

    @Override
    public void decode(PlayerProtocol protocol) {
    }

    @Override
    public void encode(PlayerProtocol protocol) {
        this.reset(protocol);
        putGameRules(gameRules);
    }
}
