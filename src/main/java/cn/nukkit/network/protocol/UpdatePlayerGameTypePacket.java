package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class UpdatePlayerGameTypePacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.UPDATE_PLAYER_GAME_TYPE_PACKET;

    public GameType gameType;
    public long entityId;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.gameType = GameType.from(this.getVarInt());
        this.entityId = this.getVarLong();
    }

    @Override
    public void encode() {
        this.reset();
        this.putVarInt(this.gameType.ordinal());
        this.putVarLong(entityId);
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

        public static GameType from(int id) {
            return VALUES[id];
        }
    }
}
