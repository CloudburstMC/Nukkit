package cn.nukkit.network.protocol;

import cn.nukkit.math.Vector3f;
import cn.nukkit.utils.Binary;
import io.netty.buffer.ByteBuf;
import lombok.ToString;

/**
 * Created on 15-10-14.
 */
@ToString
public class MovePlayerPacket extends DataPacket {

    public static final short NETWORK_ID = ProtocolInfo.MOVE_PLAYER_PACKET;

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
    protected void decode(ByteBuf buffer) {
        this.eid = Binary.readEntityRuntimeId(buffer);
        Vector3f v = Binary.readVector3f(buffer);
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        this.pitch = buffer.readFloatLE();
        this.yaw = buffer.readFloatLE();
        this.headYaw = buffer.readFloatLE();
        this.mode = buffer.readByte();
        this.onGround = buffer.readBoolean();
        this.ridingEid = Binary.readEntityRuntimeId(buffer);
        if (this.mode == MODE_TELEPORT) {
            this.int1 = buffer.readIntLE();
            this.int2 = buffer.readIntLE();
        }
    }

    @Override
    protected void encode(ByteBuf buffer) {
        Binary.writeEntityRuntimeId(buffer, this.eid);
        Binary.writeVector3f(buffer, this.x, this.y, this.z);
        buffer.writeFloatLE(this.pitch);
        buffer.writeFloatLE(this.yaw);
        buffer.writeFloatLE(this.headYaw);
        buffer.writeByte((byte) this.mode);
        buffer.writeBoolean(this.onGround);
        Binary.writeEntityRuntimeId(buffer, this.ridingEid);
        if (this.mode == MODE_TELEPORT) {
            buffer.writeIntLE(this.int1);
            buffer.writeIntLE(this.int2);
        }
    }

    @Override
    public short pid() {
        return NETWORK_ID;
    }

}
