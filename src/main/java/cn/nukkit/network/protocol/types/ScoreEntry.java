package cn.nukkit.network.protocol.types;

public class ScoreEntry {

    public static final byte TYPE_PLAYER = 1;
    public static final byte TYPE_ENTITY = 2;
    public static final byte TYPE_FAKE_PLAYER = 3;

    public long scoreId;
    public String objectiveName;
    public int score;

    public byte type;

    public long entityUniqueId;
    public String displayName;

}
