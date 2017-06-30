package cn.nukkit.network.protocol;

import cn.nukkit.math.Vector3f;

/**
 * Created on 15-10-14.
 */
public class MovePlayerPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.MOVE_PLAYER_PACKET;

    public static final byte MODE_NORMAL = 0;
    public static final byte MODE_RESET = 1;
    public static final byte MODE_TELEPORT = 2;
    public static final byte MODE_PITCH = 3; //facepalm Mojang

    public long eid;
    public float x;
    public float y;
    public float z;
    public float yaw;
    public float headYaw;
    public float pitch;
    public byte mode = MODE_NORMAL;
    public boolean onGround;
    public long ridingEid;
    public int int1 = 0;
    public int int2 = 0;

    @Override
    public void decode() {
        this.eid = this.getVarLong();
        Vector3f v = this.getVector3f();
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        this.pitch = this.getLFloat();
        this.headYaw = this.getLFloat();
        this.yaw = this.getLFloat();
        this.mode = (byte) this.getByte();
        this.onGround = this.getBoolean();
        this.ridingEid = this.getVarLong();
        if (this.mode == MODE_TELEPORT){
            this.int1 = this.getLInt();
            this.int2 = this.getLInt();
        }
    }

    @Override
    public void encode() {
        this.reset();
        this.putVarLong(this.eid);
        this.putVector3f(this.x, this.y, this.z);
        this.putLFloat(this.pitch);
        this.putLFloat(this.yaw);
        this.putLFloat(this.headYaw);
        this.putByte(this.mode);
        this.putBoolean(this.onGround);
        this.putVarLong(this.ridingEid);
        if (this.mode == MODE_TELEPORT){
            this.putLInt(this.int1);
            this.putLInt(this.int2);
        }
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

}
