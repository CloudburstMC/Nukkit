package cn.nukkit.network.protocol;

import cn.nukkit.math.Vector3f;
import lombok.ToString;

@ToString
public class PlayerAuthInputPacket extends DataPacket {

    public float pitch;
    public float yaw;
    public Vector3f position;
    public float moveX;
    public float moveZ;
    public float headYaw;
    public long inputFlags;
    public int inputMode;
    public PlayMode playMode;
    public Vector3f vrGazeDirection = null;
    public long tick;
    public Vector3f delta;

    @Override
    public byte pid() {
        return ProtocolInfo.PLAYER_AUTH_INPUT_PACKET;
    }

    @Override
    public void decode() {
        this.pitch = this.getFloat();
        this.yaw = this.getFloat();
        this.position = this.getVector3f();
        this.moveX = this.getFloat();
        this.moveZ = this.getFloat();
        this.headYaw = this.getFloat();
        this.inputFlags = this.getUnsignedVarLong();
        this.inputMode = (int) this.getUnsignedVarInt();
        this.playMode = PlayMode.values()[(int) this.getUnsignedVarInt()];
        if (this.playMode == PlayMode.VR) {
            this.vrGazeDirection = this.getVector3f();
        }
        this.tick = this.getUnsignedVarLong();
        this.delta = this.getVector3f();
    }

    @Override
    public void encode() {
        this.reset();
        this.putFloat(this.pitch);
        this.putFloat(this.yaw);
        this.putVector3f(this.position);
        this.putFloat(this.moveX);
        this.putFloat(this.moveZ);
        this.putFloat(this.headYaw);
        this.putUnsignedVarLong(this.inputFlags);
        this.putUnsignedVarInt(this.inputMode);
        this.putUnsignedVarInt(this.playMode.ordinal());
        if (this.playMode == PlayMode.VR) {
            this.putVector3(this.vrGazeDirection);
        }
        this.putUnsignedVarLong(this.tick);
        this.putVector3(this.delta);
    }

    public static enum PlayMode {

        NORMAL,
        TEASER,
        SCREEN,
        VIEWER,
        VR,
        PLACEMENT,
        LIVING_ROOM,
        EXIT_LEVEL,
        EXIT_LEVEL_LIVING_ROOM
    }
}
