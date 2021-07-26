package cn.nukkit.network.protocol;

import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

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
    // see x
    private int deltaX;
    @Deprecated
    // see y
    private int deltaY;
    @Deprecated
    // see z
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
        this.putEntityRuntimeId(this.runtimeEntityId);
        if (flags != 0) {
            this.putLShort(flags);
            if ((this.flags & FLAG_HAS_X) != 0) {
                this.putLFloat(this.x);
            }
            if ((this.flags & FLAG_HAS_Y) != 0) {
                this.putLFloat(this.y);
            }
            if ((this.flags & FLAG_HAS_Z) != 0) {
                this.putLFloat(this.z);
            }
            if ((this.flags & FLAG_HAS_PITCH) != 0) {
                this.putByte((byte) (this.pitch / (360F / 256F)));
            }
            if ((this.flags & FLAG_HAS_YAW) != 0) {
                this.putByte((byte) (this.yaw / (360F / 256F)));
            }
            if ((this.flags & FLAG_HAS_HEAD_YAW) != 0) {
                this.putByte((byte) (this.headYaw / (360F / 256F)));
            }
        } else {
            List<Consumer<MoveEntityDeltaPacket>> consumers = new ArrayList<>(6);
            if (this.x != 0) {
                this.flags |= FLAG_HAS_X;
                consumers.add(packet -> packet.putLFloat(this.x));
            }
            if (this.y != 0) {
                this.flags |= FLAG_HAS_Y;
                consumers.add(packet -> packet.putLFloat(this.y));
            }
            if (this.z != 0) {
                this.flags |= FLAG_HAS_Z;
                consumers.add(packet -> packet.putLFloat(this.z));
            }
            if (this.pitch != 0) {
                this.flags |= FLAG_HAS_PITCH;
                consumers.add(packet -> packet.putByte((byte) (this.pitch / (360F / 256F))));
            }
            if (this.yaw != 0) {
                this.flags |= FLAG_HAS_YAW;
                consumers.add(packet -> packet.putByte((byte) (this.yaw / (360F / 256F))));
            }
            if (this.headYaw != 0) {
                this.flags |= FLAG_HAS_HEAD_YAW;
                consumers.add(packet -> packet.putByte((byte) (this.headYaw / (360F / 256F))));
            }
            this.putLShort(this.flags);
            if (consumers.size() > 0) {
                for (Consumer<MoveEntityDeltaPacket> consumer : consumers) {
                    consumer.accept(this);
                }
            }
        }
    }

    public float getCoordinate() {
        return this.getLFloat();
    }

    public void putCoordinate(float value) {
        this.putLFloat(value);
    }

    public float getRotation() {
        return this.getByte() * (360F / 256F);
    }

    public void putRotation(float value) {
        this.putByte((byte) (value / (360F / 256F)));
    }

    public boolean hasFlag(int flag) {
        return (this.flags & flag) != 0;
    }
}
