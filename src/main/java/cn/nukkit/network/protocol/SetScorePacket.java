package cn.nukkit.network.protocol;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@ToString
public class SetScorePacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.SET_SCORE_PACKET;

    public final List<ScoreInfo> infos = new ObjectArrayList<>();

    private static final String[] TYPES = {"remove", "changeplayer", "changeentity", "changefakeplayer"};

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

        this.putUnsignedVarInt(this.infos.size());

        for (ScoreInfo info : this.infos) {
            this.putUnsignedVarInt(info.type.ordinal());
            this.putString(TYPES[info.type.ordinal()]);

            this.putVarLong(info.scoreboardId);

            switch (info.type) {
                case INVALID:
                    if (info.objectiveId != null && !info.objectiveId.isEmpty()) {
                        this.putBoolean(true);
                        this.putString(info.objectiveId);
                    } else {
                        this.putBoolean(false);
                    }
                    break;
                case PLAYER:
                case ENTITY:
                    this.putString(info.objectiveId);
                    this.putLInt(info.score);
                    this.putEntityUniqueId(info.entityId);
                    break;
                case FAKE:
                    this.putString(info.objectiveId);
                    this.putLInt(info.score);
                    this.putString(info.name);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid score info type");
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
         * Score info for score removal
         * @param scoreboardId scoreboard id
         * @param objectiveId objective id
         * @param score score
         */
        public ScoreInfo(long scoreboardId, String objectiveId, int score) {
            this.scoreboardId = scoreboardId;
            this.objectiveId = objectiveId;
            this.score = score;
            this.type = ScorerType.INVALID;
            this.name = null;
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
