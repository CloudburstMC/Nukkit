package cn.nukkit.network.protocol.types;

public class ScoreInfo {

    public long scoreboardId;
    public String objectiveId;
    public int score;
    public ScorerType type;
    public String name;
    public long entityId;

    public enum ScorerType {
        INVALID,
        PLAYER,
        ENTITY,
        FAKE
    }

}
