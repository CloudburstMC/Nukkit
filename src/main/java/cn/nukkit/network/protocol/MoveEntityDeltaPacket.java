package cn.nukkit.network.protocol;

import cn.nukkit.utils.Binary;
import io.netty.buffer.ByteBuf;
import lombok.ToString;

@ToString
public class MoveEntityDeltaPacket extends DataPacket {
    public static final short NETWORK_ID = ProtocolInfo.MOVE_ENTITY_DELTA_PACKET;

    public static final int FLAG_HAS_X = 0b1;
    public static final int FLAG_HAS_Y = 0b10;
    public static final int FLAG_HAS_Z = 0b100;
    public static final int FLAG_HAS_YAW = 0b1000;
    public static final int FLAG_HAS_HEAD_YAW = 0b10000;
    public static final int FLAG_HAS_PITCH = 0b100000;

    public int flags = 0;
    public int xDelta = 0;
    public int yDelta = 0;
    public int zDelta = 0;
    public double yawDelta = 0;
    public double headYawDelta = 0;
    public double pitchDelta = 0;

    @Override
    public short pid() {
        return NETWORK_ID;
    }

    @Override
    protected void decode(ByteBuf buffer) {
        this.flags = buffer.readByte();
        this.xDelta = getCoordinate(buffer, FLAG_HAS_X);
        this.yDelta = getCoordinate(buffer, FLAG_HAS_Y);
        this.zDelta = getCoordinate(buffer, FLAG_HAS_Z);
        this.yawDelta = getRotation(buffer, FLAG_HAS_YAW);
        this.headYawDelta = getRotation(buffer, FLAG_HAS_HEAD_YAW);
        this.pitchDelta = getRotation(buffer, FLAG_HAS_PITCH);
    }

    @Override
    protected void encode(ByteBuf buffer) {
        buffer.writeByte(flags);
        putCoordinate(buffer, FLAG_HAS_X, this.xDelta);
        putCoordinate(buffer, FLAG_HAS_Y, this.yDelta);
        putCoordinate(buffer, FLAG_HAS_Z, this.zDelta);
        putRotation(buffer, FLAG_HAS_YAW, this.yawDelta);
        putRotation(buffer, FLAG_HAS_HEAD_YAW, this.headYawDelta);
        putRotation(buffer, FLAG_HAS_PITCH, this.pitchDelta);
    }

    private int getCoordinate(ByteBuf buffer, int flag) {
        if ((flags & flag) != 0) {
            return Binary.readVarInt(buffer);
        }
        return 0;
    }

    private double getRotation(ByteBuf buffer, int flag) {
        if ((flags & flag) != 0) {
            return buffer.readByte() * (360d / 256d);
        }
        return 0d;
    }

    private void putCoordinate(ByteBuf buffer, int flag, int value) {
        if ((flags & flag) != 0) {
            Binary.writeVarInt(buffer, value);
        }
    }

    private void putRotation(ByteBuf buffer, int flag, double value) {
        if ((flags & flag) != 0) {
            buffer.writeByte((byte) (value / (360d / 256d)));
        }
    }
}
