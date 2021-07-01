package cn.nukkit.network.protocol;

import cn.nukkit.math.Vector3;
import lombok.ToString;

@ToString
public class PlayerAuthInputPacket extends DataPacket {

    public static final int NORMAL = 0;
    public static final int TEASER = 1;
    public static final int SCREEN = 2;
    public static final int VIEWER = 3;
    public static final int VR = 4;
    public static final int PLACEMENT = 5;
    public static final int LIVING_ROOM = 6;
    public static final int EXIT_LEVEL = 7;
    public static final int EXIT_LEVEL_LIVING_ROOM = 8;

    public float pitch;
    public float yaw;
    public Vector3 position;
    public float moveX;
    public float moveZ;
    public float headYaw;
    public long inputFlags;
    public int inputMode;
    public int playMode;
    public Vector3 vrGazeDirection = null;
    public long tick;
    public Vector3 delta;

    @Override
    public byte pid() {
        return ProtocolInfo.PLAYER_AUTH_INPUT_PACKET;
    }

    @Override
    public void decode() {
        this.pitch = this.getFloat();
        this.yaw = this.getFloat();
        this.position = this.getVector3();
        this.moveX = this.getFloat();
        this.moveZ = this.getFloat();
        this.headYaw = this.getFloat();
        this.inputFlags = this.getUnsignedVarLong();
        this.inputMode = (int) this.getUnsignedVarInt();
        this.playMode = (int) this.getUnsignedVarInt();
        if (this.playMode == VR) {
            this.vrGazeDirection = this.getVector3();
        }
        this.tick = this.getUnsignedVarLong();
        this.delta = this.getVector3();
    }

    @Override
    public void encode() {
        this.reset();
        this.putFloat(this.pitch);
        this.putFloat(this.yaw);
        this.putVector3(this.position);
        this.putFloat(this.moveX);
        this.putFloat(this.moveZ);
        this.putFloat(this.headYaw);
        this.putUnsignedVarLong(this.inputFlags);
        this.putUnsignedVarInt(this.inputMode);
        this.putUnsignedVarInt(this.playMode);
        if (this.playMode == VR) {
            this.putVector3(this.vrGazeDirection);
        }
        this.putUnsignedVarLong(this.tick);
        this.putVector3(this.delta);
    }
}
