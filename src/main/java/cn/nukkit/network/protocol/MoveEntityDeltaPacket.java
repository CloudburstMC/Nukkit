package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class MoveEntityDeltaPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.MOVE_ENTITY_DELTA_PACKET;

    public static final int FLAG_HAS_X = 0b1;
    public static final int FLAG_HAS_Y = 0b10;
    public static final int FLAG_HAS_Z = 0b100;
    public static final int FLAG_HAS_YAW = 0b1000;
    public static final int FLAG_HAS_HEAD_YAW = 0b10000;
    public static final int FLAG_HAS_PITCH = 0b100000;

    public long eid;
    public int flags;
    public float x;
    public float y;
    public float z;
    public double yawDelta;
    public double headYawDelta;
    public double pitchDelta;

    private boolean onGround;
    private boolean forceMove;
    private boolean forceMoveLocalEntity;
    private boolean forceCompletion;

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
        this.putEntityRuntimeId(this.eid);

        putCoordinate(FLAG_HAS_X, this.x);
        putCoordinate(FLAG_HAS_Y, this.y);
        putCoordinate(FLAG_HAS_Z, this.z);
        putRotation(FLAG_HAS_YAW, this.yawDelta);
        putRotation(FLAG_HAS_HEAD_YAW, this.headYawDelta);
        putRotation(FLAG_HAS_PITCH, this.pitchDelta);

        this.putBoolean(this.onGround);
        this.putBoolean(this.forceMove);
        this.putBoolean(this.forceMoveLocalEntity);
        this.putBoolean(this.forceCompletion);
    }

    private void putCoordinate(int flag, float value) {
        if ((flags & flag) != 0) {
            this.putBoolean(true);
            this.putLFloat(value);
        } else {
            this.putBoolean(false);
        }
    }

    private void putRotation(int flag, double value) {
        if ((flags & flag) != 0) {
            this.putByte((byte) (value / 1.40625));
        }
    }
}
