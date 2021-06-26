package cn.nukkit.network.protocol;

import cn.nukkit.math.Vector3f;
import lombok.ToString;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@ToString
public class SetEntityMotionPacket extends DataPacket {

    public long entityRuntimeId;
    public Vector3f motion;

    @Override
    public byte pid() {
        return ProtocolInfo.SET_ENTITY_MOTION_PACKET;
    }

    @Override
    public void decode() {
    	this.entityRuntimeId = this.getEntityRuntimeId();
		this.motion = this.getVector3f();
    }

    @Override
    public void encode() {
        this.reset();
        this.putEntityRuntimeId(this.entityRuntimeId);
		this.putVector3f(this.motion);
    }
}
