package cn.nukkit.network.protocol;

import cn.nukkit.utils.RuleData;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class GameRulesChangedPacket extends DataPacket {

    @Override
    public byte pid(PlayerProtocol protocol) {
        return protocol.equals(PlayerProtocol.PLAYER_PROTOCOL_113) ?
                ProtocolInfo113.GAME_RULES_CHANGED_PACKET :
                ProtocolInfo.GAME_RULES_CHANGED_PACKET;
    }

    public RuleData[] ruleDatas = new RuleData[0];

    @Override
    public void decode(PlayerProtocol protocol) {
    }

    @Override
    public void encode(PlayerProtocol protocol) {
        this.reset(protocol);
        this.putVarInt(this.ruleDatas.length);
        for (RuleData rule : this.ruleDatas) {
            this.putRuleData(rule);
        }
    }
}
