package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class MoveEntityDeltaPacket extends DataPacket {

    public static final int FLAG_HAS_X = 0b01;
    public static final int FLAG_HAS_Y = 0b10;
    public static final int FLAG_HAS_Z = 0b100;
    public static final int FLAG_HAS_YAW= 0b1000;
    public static final int FLAG_HAS_HEADYAW= 0b10000;
    public static final int FLAG_HAS_PITCH = 0b100000;
    public static final int FLAG_GROUND = 0b1000000;
    public static final int FLAG_TELEPORT = 0b10000000;
    public static final int FLAG_FORCE_MOVE_LOCAL_ENTITY = 0b100000000;

    public long entityRuntimeId;
    public int flags;
    public float x = 0f;
    public float y = 0f;
    public float z = 0f;
    public double yaw = 0d;
    public double headYaw = 0d;
    public double pitch = 0d;

    @Override
    public byte pid() {
        return ProtocolInfo.MOVE_ENTITY_DELTA_PACKET;
    }

    @Override
    public void decode() {
        this.entityRuntimeId = this.getEntityRuntimeId();
        this.flags = this.getLShort();
        this.xPos = this.getCoordinate(FLAG_HAS_X);
        this.yPos = this.getCoordinate(FLAG_HAS_Y);
        this.zPos = this.getCoordinate(FLAG_HAS_Z);
        this.yaw = this.getRotation(FLAG_HAS_YAW);
        this.headYaw = this.getRotation(FLAG_HAS_HEADYAW);
        this.pitch = this.getRotation(FLAG_HAS_PITCH);
    }

    @Override
    public void encode() {
        this.putEntityRuntimeId(this.entityRuntimeId);
        this.putLShort(this.flags);
        this.putCoordinate(FLAG_HAS_X, this.z);
        this.putCoordinate(FLAG_HAS_Y, this.y);
        this.putCoordinate(FLAG_HAS_Z, this.x);
        this.putRotation(FLAG_HAS_YAW, this.yaw);
        this.putRotation(FLAG_HAS_HEADYAW, this.headYaw);
        this.putRotation(FLAG_HAS_PITCH, this.pitch);
    }

    private float getCoordinate(int flag) {
        if ((this.flags & flag) != 0) {
            return this.getLFloat();
        }
        return 0f;
    }

    private double getRotation(int flag) {
        if ((this.flags & flag) != 0) {
            return this.getByte() * (360d / 256d);
        }
        return 0d;
    }

    private void putCoordinate(int flag, float value) {
        if ((this.flags & flag) != 0) {
            this.putLFloat(value);
        }
    }

    private void putRotation(int flag, double value) {
        if ((this.flags & flag) != 0) {
            this.putByte((byte) (value / (360d / 256d)));
        }
    }
}
