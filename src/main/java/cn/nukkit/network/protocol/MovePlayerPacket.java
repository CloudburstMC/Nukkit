package cn.nukkit.network.protocol;

import cn.nukkit.Player;
import cn.nukkit.entity.item.EntityBoat;
import cn.nukkit.math.Vector3;
import cn.nukkit.math.Vector3f;

/**
 * Created on 15-10-14.
 */
public class MovePlayerPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.MOVE_PLAYER_PACKET;

    public static final int MODE_NORMAL = 0;
    public static final int MODE_RESET = 1;
    public static final int MODE_TELEPORT = 2;
    public static final int MODE_PITCH = 3; //facepalm Mojang

    public long eid;
    public float x;
    public float y;
    public float z;
    public float yaw;
    public float headYaw;
    public float pitch;
    public int mode = MODE_NORMAL;
    public boolean onGround;
    public long ridingEid;
    public int int1 = 0;
    public int int2 = 0;

    @Override
    public void decode() {
        this.eid = this.getEntityRuntimeId();
        Vector3f v = this.getVector3f();
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        this.pitch = this.getLFloat();
        this.headYaw = this.getLFloat();
        this.yaw = this.getLFloat();
        this.mode = this.getByte();
        this.onGround = this.getBoolean();
        this.ridingEid = this.getEntityRuntimeId();
        if (this.mode == MODE_TELEPORT) {
            this.int1 = this.getLInt();
            this.int2 = this.getLInt();
        }
    }

    @Override
    public void encode() {
        this.reset();
        this.putEntityRuntimeId(this.eid);
        this.putVector3f(this.x, this.y, this.z);
        this.putLFloat(this.pitch);
        this.putLFloat(this.yaw);
        this.putLFloat(this.headYaw);
        this.putByte((byte) this.mode);
        this.putBoolean(this.onGround);
        this.putEntityRuntimeId(this.ridingEid);
        if (this.mode == MODE_TELEPORT) {
            this.putLInt(this.int1);
            this.putLInt(this.int2);
        }
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void handle(Player player) {
        if (player.teleportPosition != null) {
            return;
        }

        Vector3 newPos = new Vector3(this.x, this.y - player.getEyeHeight(), this.z);

        if (newPos.distanceSquared(player) < 0.01 && this.yaw % 360 == this.yaw && this.pitch % 360 == this.pitch) {
            return;
        }

        boolean revert = false;
        if (!player.isAlive() || !player.spawned) {
            revert = true;
            player.forceMovement = new Vector3(this.x, this.y, this.z);
        }

        if (player.forceMovement != null && (newPos.distanceSquared(player.forceMovement) > 0.1 || revert)) {
            player.sendPosition(player.forceMovement, this.yaw, this.pitch, MovePlayerPacket.MODE_RESET);
        } else {
            this.yaw %= 360;
            this.pitch %= 360;

            if (this.yaw < 0) {
                this.yaw += 360;
            }

            player.setRotation(this.yaw, this.pitch);
            player.newPosition = newPos;
            player.forceMovement = null;
        }

        if (player.riding != null) {
            if (player.riding instanceof EntityBoat) {
                player.riding.setPositionAndRotation(player.temporalVector.setComponents(this.x, this.y - 1, this.z), (this.headYaw + 90) % 360, 0);
            }
        }
    }
}
