package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class UpdatePlayerGameTypePacket extends DataPacket {

    public GameType gameType;
    public long playerUniqueId;

    @Override
    public byte pid() {
        return ProtocolInfo.UPDATE_PLAYER_GAME_TYPE_PACKET;
    }

    @Override
    public void decode() {
        this.gameType = GameType.fromById(this.getVarInt());
        this.playerUniqueId = this.getEntityUniqueId();
    }

    @Override
    public void encode() {
        this.reset();
        this.putVarInt(this.gameType.ordinal());
        this.putEntityUniqueId(this.playerUniqueId);
    }

    public enum GameType {

        SURVIVAL,
        CREATIVE,
        ADVENTURE,
        SURVIVAL_VIEWER,
        CREATIVE_VIEWER,
        DEFAULT,
        WORLD_DEFAULT;

        private static final GameType[] VALUES = values();

        public static GameType fromById(int id) {
            return VALUES[id];
        }
    }
}
