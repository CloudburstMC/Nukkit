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
    public int flags = 0;
    public float x = 0;
    public float y = 0;
    public float z = 0;
    public double yawDelta = 0;
    public double headYawDelta = 0;
    public double pitchDelta = 0;

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
        this.putByte((byte) flags);
        putCoordinate(FLAG_HAS_X, this.x);
        putCoordinate(FLAG_HAS_Y, this.y);
        putCoordinate(FLAG_HAS_Z, this.z);
        putRotation(FLAG_HAS_YAW, this.yawDelta);
        putRotation(FLAG_HAS_HEAD_YAW, this.headYawDelta);
        putRotation(FLAG_HAS_PITCH, this.pitchDelta);
    }

    private void putCoordinate(int flag, float value) {
        if ((flags & flag) != 0) {
            this.putLFloat(value);
        }
    }

    private void putRotation(int flag, double value) {
        if ((flags & flag) != 0) {
            this.putByte((byte) (value / 1.40625));
        }
    }
}
