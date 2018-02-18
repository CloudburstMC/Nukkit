package cn.nukkit.network.protocol;

import cn.nukkit.math.Vector3f;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class MoveEntityPacket extends DataPacket {

    public long eid;
    public double x;
    public double y;
    public double z;
    public double yaw;
    public double headYaw;
    public double pitch;
    public boolean onGround;
    public boolean teleport;

    @Override
    public byte pid(PlayerProtocol protocol) {
        return protocol.getPacketId("MOVE_ENTITY_PACKET");
    }

    @Override
    public void decode(PlayerProtocol protocol) {
        this.eid = this.getEntityRuntimeId();
        Vector3f v = this.getVector3f();
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        this.pitch = this.getByte() * (360d / 256d);
        this.headYaw = this.getByte() * (360d / 256d);
        this.yaw = this.getByte() * (360d / 256d);
        this.onGround = this.getBoolean();
        this.teleport = this.getBoolean();
    }

    @Override
    public void encode(PlayerProtocol protocol) {
        this.reset(protocol);
        this.putEntityRuntimeId(this.eid);
        this.putVector3f((float) this.x, (float) this.y, (float) this.z);
        this.putByte((byte) (this.pitch / (360d / 256d)));
        this.putByte((byte) (this.headYaw / (360d / 256d)));
        this.putByte((byte) (this.yaw / (360d / 256d)));
        this.putBoolean(this.onGround);
        this.putBoolean(this.teleport);
    }
}
