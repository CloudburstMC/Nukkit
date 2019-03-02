package cn.nukkit.network.protocol;

import cn.nukkit.math.Vector3f;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class SetEntityMotionPacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.SET_ENTITY_MOTION_PACKET;

    public long eid;
    public float motionX;
    public float motionY;
    public float motionZ;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.eid = this.getEntityRuntimeId();
        Vector3f v = this.getVector3f();
        this.motionX = v.x;
        this.motionY = v.y;
        this.motionZ = v.z;
    }

    @Override
    public void encode() {
        this.reset();
        this.putEntityRuntimeId(this.eid);
        this.putVector3f(this.motionX, this.motionY, this.motionZ);
    }
}
