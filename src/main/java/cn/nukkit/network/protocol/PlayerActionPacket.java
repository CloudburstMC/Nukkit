package cn.nukkit.network.protocol;

/**
 * @author Nukkit Project Team
 */
public class PlayerActionPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.PLAYER_ACTION_PACKET;

    public static final byte ACTION_START_BREAK = 0;
    public static final byte ACTION_ABORT_BREAK = 1;
    public static final byte ACTION_STOP_BREAK = 2;


    public static final byte ACTION_RELEASE_ITEM = 5;
    public static final byte ACTION_STOP_SLEEPING = 6;
    public static final byte ACTION_RESPAWN = 7;
    public static final byte ACTION_JUMP = 8;
    public static final byte ACTION_START_SPRINT = 9;
    public static final byte ACTION_STOP_SPRINT = 10;
    public static final byte ACTION_START_SNEAK = 11;
    public static final byte ACTION_STOP_SNEAK = 12;
    public static final byte ACTION_DIMENSION_CHANGE = 13;

    public static final byte ACTION_NETHER_UNKNOWN = 14; //todo what's this?

    public long entityId;
    public int action;
    public int x;
    public int y;
    public int z;
    public int face;


    @Override
    public void decode() {
        entityId = getLong();
        action = getInt();
        x = getInt();
        y = getInt();
        z = getInt();
        face = getInt();
    }

    @Override
    public void encode() {
        reset();
        putLong(entityId);
        putInt(action);
        putInt(x);
        putInt(y);
        putInt(z);
        putInt(face);
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

}
