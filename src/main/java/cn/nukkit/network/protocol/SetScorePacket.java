package cn.nukkit.network.protocol;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@ToString
public class SetScorePacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.SET_SCORE_PACKET;

    public Action action;
    public final List<ScoreInfo> infos = new ObjectArrayList<>();

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.decodeUnsupported();
    }

    @Override
    public void encode() {
        this.reset();
        this.putByte((byte) this.action.ordinal());
        this.putUnsignedVarInt(this.infos.size());

        for (ScoreInfo info : this.infos) {
            this.putVarLong(info.scoreboardId);
            this.putString(info.objectiveId);
            this.putLInt(info.score);

            if (this.action == Action.SET) {
                this.putByte((byte) info.type.ordinal());

                switch (info.type) {
                    case PLAYER:
                    case ENTITY:
                        this.putEntityUniqueId(info.entityId);
                        break;
                    case FAKE:
                        this.putString(info.name);
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid score info type");
                }
            }
        }
    }

    public enum Action {
        SET,
        REMOVE
    }

    @Getter
    @EqualsAndHashCode
    @ToString
    public static class ScoreInfo {

        private final long scoreboardId;
        private final String objectiveId;
        private final int score;
        private final ScorerType type;
        private final String name;
        private final long entityId;

        /**
         * Score info for fake player
         * @param scoreboardId scoreboard id
         * @param objectiveId objective id
         * @param score score
         * @param name line text
         */
        public ScoreInfo(long scoreboardId, String objectiveId, int score, String name) {
            this.scoreboardId = scoreboardId;
            this.objectiveId = objectiveId;
            this.score = score;
            this.type = ScorerType.FAKE;
            this.name = name;
            this.entityId = -1;
        }

        /**
         * Score info for player/entity
         * @param scoreboardId scoreboard id
         * @param objectiveId objective id
         * @param type entity type; PLAYER or ENTITY
         * @param score score
         * @param entityId entity id
         */
        public ScoreInfo(long scoreboardId, String objectiveId, int score, ScorerType type, long entityId) {
            if (type != ScorerType.PLAYER && type != ScorerType.ENTITY) {
                throw new IllegalArgumentException("Scorer type must be either PLAYER or ENTITY");
            }

            this.scoreboardId = scoreboardId;
            this.objectiveId = objectiveId;
            this.score = score;
            this.type = type;
            this.name = null;
            this.entityId = entityId;
        }

        public enum ScorerType {
            INVALID,
            PLAYER,
            ENTITY,
            FAKE
        }
    }
}
