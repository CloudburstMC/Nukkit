package cn.nukkit.network.protocol;

import cn.nukkit.math.Vector3f;
import lombok.ToString;

/**
 * @author Nukkit Project Team
 */
@ToString
public class RespawnPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.RESPAWN_PACKET;

    public Vector3f position;
    public State state = State.SEARCHING_FOR_SPAWN;
    public long entityRuntimeId;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.position = this.getVector3f();
        this.respawnState = State.values()[this.getByte()];
        this.runtimeEntityId = this.getEntityRuntimeId();
    }

    @Override
    public void encode() {
        this.reset();
        this.putVector3f(this.position);
        this.putByte(this.respawnState.ordinal());
        this.putEntityRuntimeId(this.runtimeEntityId);
    }

    public static enum State {

        SEARCHING_FOR_SPAWN,
        READY_TO_SPAWN,
        CLIENT_READY_TO_SPAWN
    }
}
