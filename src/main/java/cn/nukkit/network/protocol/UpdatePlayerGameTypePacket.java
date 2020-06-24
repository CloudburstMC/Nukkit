package cn.nukkit.network.protocol;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

@PowerNukkitOnly
@Since("1.2.2.0-PN")
public class UpdatePlayerGameTypePacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.UPDATE_PLAYER_GAME_TYPE_PACKET;
    
    public int gameType;
    public long entityId;

    @Override
    public void encode() {
        putVarInt(gameType);
        putVarLong(entityId);
    }

    @Override
    public void decode() {
        gameType = getVarInt();
        entityId = getVarLong();
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }
}
