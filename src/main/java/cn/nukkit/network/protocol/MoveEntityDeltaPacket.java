package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class MoveEntityDeltaPacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.MOVE_ENTITY_DELTA_PACKET;

    public static final int FLAG_HAS_X = 0B1;
    public static final int FLAG_HAS_Y = 0B10;
    public static final int FLAG_HAS_Z = 0B100;
    public static final int FLAG_HAS_PITCH = 0B1000;
    public static final int FLAG_HAS_YAW = 0B10000;
    public static final int FLAG_HAS_HEAD_YAW = 0B100000;
    public static final int FLAG_ON_GROUND = 0B1000000;
    public static final int FLAG_TELEPORTING = 0B10000000;
    public static final int FLAG_FORCE_MOVE_LOCAL_ENTITY = 0B100000000;

    public long runtimeEntityId;
    public int flags = 0;
    public float x = 0;
    public float y = 0;
    public float z = 0;
    public float pitch = 0;
    public float yaw = 0;
    public float headYaw = 0;
    @Deprecated
    private int deltaX;
    @Deprecated
    private int deltaY;
    @Deprecated
    private int deltaZ;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.runtimeEntityId = this.getEntityRuntimeId();
        this.flags = this.getByte();
        if ((flags & FLAG_HAS_X) != 0) {
            this.x = this.getLFloat();
        }
        if ((flags & FLAG_HAS_Y) != 0) {
            this.y = this.getLFloat();
        }
        if ((flags & FLAG_HAS_Z) != 0) {
            this.z = this.getLFloat();
        }
        if ((flags & FLAG_HAS_PITCH) != 0) {
            this.pitch = this.getByte() * (360F / 256F);
        }
        if ((flags & FLAG_HAS_YAW) != 0) {
            this.yaw = this.getByte() * (360F / 256F);
        }
        if ((flags & FLAG_HAS_HEAD_YAW) != 0) {
            this.headYaw = this.getByte() * (360F / 256F);
        }
    }

    @Override
    public void encode() {
        this.reset();
        this.putEntityRuntimeId(runtimeEntityId);
        //int flagsIndex = buffer.writerIndex();
        this.putLShort(0);
        int flags = 0;
        if ((this.flags & FLAG_HAS_X) != 0) {
            flags |= FLAG_HAS_X;
            this.putLFloat(this.x);
        }
        if ((this.flags & FLAG_HAS_Y) != 0) {
            flags |= FLAG_HAS_Y;
            this.putLFloat(this.y);
        }
        if ((this.flags & FLAG_HAS_Z) != 0) {
            flags |= FLAG_HAS_Z;
            this.putLFloat(this.z);
        }
        if ((this.flags & FLAG_HAS_PITCH) != 0) {
            flags |= FLAG_HAS_PITCH;
            this.putByte((byte) (pitch / (360F / 256F)));
        }
        if ((this.flags & FLAG_HAS_YAW) != 0) {
            flags |= FLAG_HAS_YAW;
            this.putByte((byte) (yaw / (360F / 256F)));
        }
        if ((this.flags & FLAG_HAS_HEAD_YAW) != 0) {
            flags |= FLAG_HAS_HEAD_YAW;
            this.putByte((byte) (headYaw / (360F / 256F)));
        }
        //int currentIndex = buffer.writerIndex();
        //buffer.writerIndex(flagsIndex);
        this.putByte((byte) flags);
        //buffer.writerIndex(currentIndex);
    }

    private float getCoordinate(int flag) {
        if ((flags & flag) != 0) {
            return this.getLFloat();
        }
        return 0;
    }

    private double getRotation(int flag) {
        if ((flags & flag) != 0) {
            return this.getByte() * (360F / 256F);
        }
        return 0;
    }

    private void putCoordinate(int flag, float value) {
        if ((flags & flag) != 0) {
            this.putLFloat(value);
        }
    }

    private void putRotation(int flag, double value) {
        if ((flags & flag) != 0) {
            this.putByte((byte) (value / (360F / 256F)));
        }
    }

    public boolean hasFlag(int flag) {
        return (flags & flag) != 0;
    }
}
