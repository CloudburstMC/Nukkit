package cn.nukkit.network.protocol;

import cn.nukkit.math.Vector3f;
import lombok.ToString;

/**
 * @author Nukkit Project Team
 */
@ToString
public class RespawnPacket extends DataPacket {

    public static final byte STATE_SEARCHING_FOR_SPAWN = 0;
    public static final byte STATE_READY_TO_SPAWN = 1;
    public static final byte STATE_CLIENT_READY_TO_SPAWN = 2;

    public Vector3f position;
    public byte respawnState = STATE_SEARCHING_FOR_SPAWN;
    public long entityRuntimeId;

    @Override
    public byte pid() {
        return ProtocolInfo.RESPAWN_PACKET;
    }

    @Override
    public void decode() {
        this.position = this.getVector3f();
        this.respawnState = this.getByte();
        this.runtimeEntityId = this.getEntityRuntimeId();
    }

    @Override
    public void encode() {
        this.reset();
        this.putVector3f(this.position);
        this.putByte(this.respawnState);
        this.putEntityRuntimeId(this.runtimeEntityId);
    }
}
