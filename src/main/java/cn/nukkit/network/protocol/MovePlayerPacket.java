package cn.nukkit.network.protocol;

/**
 * Created on 15-10-14.
 */
public class MovePlayerPacket extends DataPacket {

    public static final byte NETWORK_ID = Info.MOVE_PLAYER_PACKET;

    public static final byte  MODE_NORMAL = 0;
    public static final byte  MODE_RESET = 1;
    public static final byte  MODE_ROTATION = 2;
    
    public long entityId;
    public float x;
    public float y;
    public float z;
    public float yaw;
    public float bodyYaw;
    public float pitch;
    public byte mode = MODE_NORMAL;
    public boolean onGround;

    @Override
    public void decode() {
        entityId = getLong();
        x = getFloat();
        y = getFloat();
        z = getFloat();
        yaw = getFloat();
        bodyYaw = getFloat();
        pitch = getFloat();
        mode = getByte();
        onGround = getByte() > 0;
    }

    @Override
    public void encode() {
        reset();
        putLong(entityId);
        putFloat(x);
        putFloat(y);
        putFloat(z);
        putFloat(yaw);
        putFloat(bodyYaw);
        putFloat(pitch);
        putByte(mode);
        putByte(onGround ? (byte) 1 : 0);
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

}
