package cn.nukkit.network.protocol;

import cn.nukkit.math.Vector3f;
import lombok.ToString;

/**
 * Created on 15-10-14.
 */
@ToString
public class MovePlayerPacket extends DataPacket {

    public static final byte MODE_NORMAL = 0;
    public static final byte MODE_RESET = 1;
    public static final byte MODE_TELEPORT = 2;
    public static final byte MODE_PITCH = 3; //Facepalm Mojang

    public long entityRuntimeId;
    public Vector3f position;
    public float pitch;
    public float yaw;
    public float headYaw;
    public byte mode = MODE_NORMAL;
    public boolean onGround;
    public long ridingRuntimeId;
    public int teleportCause = 0;
    public int teleportItem = 0;
    public long tick;

    @Override
    public byte pid() {
        return ProtocolInfo.MOVE_PLAYER_PACKET;
    }

    @Override
    public void decode() {
        this.entityRuntimeId = this.getEntityRuntimeId();
        this.position = this.getVector3f();
        this.pitch = this.getLFloat();
        this.yaw = this.getLFloat();
        this.headYaw = this.getLFloat();
        this.mode = this.getByte();
        this.onGround = this.getBoolean();
        this.ridingRuntimeId = this.getEntityRuntimeId();
        if (this.mode == MODE_TELEPORT) {
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
        this.putByte(this.mode);
        this.putBoolean(this.onGround);
        this.putEntityRuntimeId(this.ridingRuntimeId);
        if (this.mode == MODE_TELEPORT) {
            this.putLInt(this.teleportCause);
            this.putLInt(this.teleportItem);
        }
        this.putUnsignedVarLong(this.tick);
    }
}
