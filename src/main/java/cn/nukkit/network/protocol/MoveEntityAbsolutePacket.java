package cn.nukkit.network.protocol;

import cn.nukkit.math.Vector3f;
import lombok.ToString;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@ToString
public class MoveEntityAbsolutePacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.MOVE_ENTITY_ABSOLUTE_PACKET;

    public long eid;
    public double x;
    public double y;
    public double z;
    public double yaw;
    public double headYaw;
    public double pitch;
    public boolean onGround;
    public boolean teleport;
    public boolean forceMoveLocalEntity;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.eid = this.getEntityRuntimeId();
        int flags = this.getByte();
        onGround = (flags & 0x01) != 0;
        teleport = (flags & 0x02) != 0;
        forceMoveLocalEntity = (flags & 0x04) != 0;
        Vector3f v = this.getVector3f();
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        this.pitch = this.getByte() * (360d / 256d);
        this.headYaw = this.getByte() * (360d / 256d);
        this.yaw = this.getByte() * (360d / 256d);
    }

    @Override
    public void encode() {
        this.reset();
        this.putEntityRuntimeId(this.eid);
        byte flags = 0;
        if (onGround) {
            flags |= 0x01;
        }
        if (teleport) {
            flags |= 0x02;
        }
        if (forceMoveLocalEntity) {
            flags |= 0x04;
        }
        this.putByte(flags);
        this.putVector3f((float) this.x, (float) this.y, (float) this.z);
        this.putByte((byte) (this.pitch / (360d / 256d)));
        this.putByte((byte) (this.headYaw / (360d / 256d)));
        this.putByte((byte) (this.yaw / (360d / 256d)));
    }
}
