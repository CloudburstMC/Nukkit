package cn.nukkit.network.protocol;

import cn.nukkit.network.protocol.types.ScoreInfo;

public class SetScorePacket extends DataPacket {

    public Action action;
    public ScoreInfo[] infos;

    @Override
    public byte pid() {
        return ProtocolInfo.SET_SCORE_PACKET;
    }

    @Override
    public void decode() {
        this.action = Action.values()[this.getByte()];
        this.infos = new ScoreInfo[(int) this.getUnsignedVarInt()];

        for (int index = 0; index < this.infos.length; index++) {
            ScoreInfo scoreInfo = new ScoreInfo();
            scoreInfo.scoreboardId = this.getVarLong();
            scoreInfo.objectiveId = this.getString();
            scoreInfo.score = this.getLInt();
            if (this.action == Action.SET) {
                scoreInfo.type = ScoreInfo.ScorerType.values()[this.getByte()];
                switch (scoreInfo.type) {
                    case PLAYER:
                    case ENTITY:
                        scoreInfo.entityId = this.getEntityUniqueId();
                        break;
                    case FAKE:
                        scoreInfo.name = this.getString();
                        break;
                }
            }

            this.infos[index] = scoreInfo;
        }
    }

    @Override
    public void encode() {
        this.reset();
        this.putByte((byte) this.action.ordinal());
        this.putUnsignedVarInt(this.infos.length);
        for (ScoreInfo scoreInfo : this.infos) {
            this.putVarLong(scoreInfo.scoreboardId);
            this.putString(scoreInfo.objectiveId);
            this.putLInt(scoreInfo.score);
            if (this.action == Action.SET) {
                this.putByte((byte) scoreInfo.type.ordinal());
                switch (scoreInfo.type) {
                    case PLAYER:
                    case ENTITY:
                        this.putEntityUniqueId(scoreInfo.entityId);
                        break;
                    case FAKE:
                        this.putString(scoreInfo.name);
                        break;
                }
            }
        }
    }

    public enum Action {
        SET,
        REMOVE
    }

}
