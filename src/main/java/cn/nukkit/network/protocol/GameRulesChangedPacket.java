package cn.nukkit.network.protocol;

import cn.nukkit.level.gamerule.GameRuleMap;
import cn.nukkit.utils.Binary;
import io.netty.buffer.ByteBuf;
import lombok.ToString;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@ToString
public class GameRulesChangedPacket extends DataPacket {
    public static final short NETWORK_ID = ProtocolInfo.GAME_RULES_CHANGED_PACKET;
    public GameRuleMap gameRules;

    @Override
    public short pid() {
        return NETWORK_ID;
    }

    @Override
    protected void decode(ByteBuf buffer) {
    }

    @Override
    protected void encode(ByteBuf buffer) {
        Binary.writeGameRules(buffer, gameRules);
    }
}
