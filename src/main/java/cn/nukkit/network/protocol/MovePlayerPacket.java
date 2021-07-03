package cn.nukkit.network.protocol;

import cn.nukkit.math.Vector3f;
import lombok.ToString;

/**
 * Created on 15-10-14.
 */
@ToString
public class MovePlayerPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.MOVE_PLAYER_PACKET;

    public long entityRuntimeId;
    public Vector3f position;
    public float pitch;
    public float yaw;
    public float headYaw;
    public Mode mode = Mode.NORMAL;
    public boolean onGround;
    public long ridingRuntimeId;
    public int teleportCause;
    public int teleportItem;
    public long tick;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.entityRuntimeId = this.getEntityRuntimeId();
        this.position = this.getVector3f();
        this.pitch = this.getLFloat();
        this.yaw = this.getLFloat();
        this.headYaw = this.getLFloat();
        this.mode = Mode.values()[this.getByte()];
        this.onGround = this.getBoolean();
        this.ridingRuntimeId = this.getEntityRuntimeId();
        if (this.mode == Mode.TELEPORT) {
            this.teleportCause = this.getLInt();
            this.teleportItem = this.getLInt();
        }
        this.tick = this.getUnsignedVarLong();
    }

    @Override
    public void encode() {
        this.reset();
        this.putEntityRuntimeId(this.entityRuntimeId);
        this.putVector3f(this.position);
        this.putLFloat(this.pitch);
        this.putLFloat(this.yaw);
        this.putLFloat(this.headYaw);
        this.putByte((byte) this.mode.ordinal());
        this.putBoolean(this.onGround);
        this.putEntityRuntimeId(this.ridingRuntimeId);
        if (this.mode == Mode.TELEPORT) {
            this.putLInt(this.teleportCause);
            this.putLInt(this.teleportItem);
        }
        this.putUnsignedVarLong(this.tick);
    }

    public static enum Mode {

        NORMAL,
        RESET,
        TELEPORT,
        PITCH
    }
}
