package cn.nukkit.network.protocol;

import cn.nukkit.math.Vector3f;

/**
 * Created on 15-10-14.
 */
public class MovePlayerPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.MOVE_PLAYER_PACKET;

    public static final byte MODE_NORMAL = 0;
    public static final byte MODE_RESET = 1;
    public static final byte MODE_ROTATION = 2;

    public long eid;
    public float x;
    public float y;
    public float z;
    public float yaw;
    public float headYaw;
    public float pitch;
    public byte mode = MODE_NORMAL;
    public boolean onGround;

    @Override
    public void decode() {
        eid = this.getEntityId();
        Vector3f v = this.getVector3f();
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        pitch = getLFloat();
        headYaw = getLFloat();
        yaw = getLFloat();
        mode = (byte) getByte();
        onGround = getBoolean();
    }

    @Override
    public void encode() {
        reset();
        putEntityId(eid);
        putVector3f(x, y, z);
        //putLFloat(x);
        //putLFloat(y);
        //putLFloat(z);
        putLFloat(pitch);
        putLFloat(yaw);
        putLFloat(headYaw);
        putByte(mode);
        putBoolean(onGround);
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

}
