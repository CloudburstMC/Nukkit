package cn.nukkit.network.protocol;

import cn.nukkit.math.Vector3f;
import lombok.ToString;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@ToString
public class MoveEntityAbsolutePacket extends DataPacket {

    public static final byte FLAG_GROUND = 0x01;
    public static final byte FLAG_TELEPORT = 0x02;
    public static final byte FLAG_FORCE_MOVE_LOCAL_ENTITY = 0x04;

    public long entityRuntimeId;
    public byte flags;
    public Vector3f position;
    public double yaw;
    public double headYaw;
    public double pitch;

    @Override
    public byte pid() {
        return ProtocolInfo.MOVE_ENTITY_ABSOLUTE_PACKET;
    }

    @Override
    public void decode() {
        this.entityRuntimeId = this.getEntityRuntimeId();
        this.flags = this.getByte();
        this.position = this.getVector3f();
        this.yaw = this.getByte() * (360d / 256d);
        this.headYaw = this.getByte() * (360d / 256d);
        this.pitch = this.getByte() * (360d / 256d);
    }

    @Override
    public void encode() {
        this.reset();
        this.putEntityRuntimeId(this.entityRuntimeId);
        this.putByte(this.flags);
        this.putVector3f(this.position);
        this.putByte((byte) (this.yaw / (360d / 256d)));
        this.putByte((byte) (this.headYaw / (360d / 256d)));
        this.putByte((byte) (this.pitch / (360d / 256d)));
    }
}
