package cn.nukkit.network.protocol;

import cn.nukkit.math.Vector3f;
import cn.nukkit.utils.Binary;
import io.netty.buffer.ByteBuf;
import lombok.ToString;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@ToString
public class MoveEntityAbsolutePacket extends DataPacket {
    public static final short NETWORK_ID = ProtocolInfo.MOVE_ENTITY_ABSOLUTE_PACKET;

    public long eid;
    public double x;
    public double y;
    public double z;
    public double yaw;
    public double headYaw;
    public double pitch;
    public boolean onGround;
    public boolean teleport;

    @Override
    public short pid() {
        return NETWORK_ID;
    }

    @Override
    protected void decode(ByteBuf buffer) {
        this.eid = Binary.readEntityRuntimeId(buffer);
        int flags = buffer.readByte();
        teleport = (flags & 0x01) != 0;
        onGround = (flags & 0x02) != 0;
        Vector3f v = Binary.readVector3f(buffer);
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        this.pitch = buffer.readByte() * (360d / 256d);
        this.headYaw = buffer.readByte() * (360d / 256d);
        this.yaw = buffer.readByte() * (360d / 256d);
    }

    @Override
    protected void encode(ByteBuf buffer) {
        Binary.writeEntityRuntimeId(buffer, this.eid);
        byte flags = 0;
        if (teleport) {
            flags |= 0x01;
        }
        if (onGround) {
            flags |= 0x02;
        }
        buffer.writeByte(flags);
        Binary.writeVector3f(buffer, (float) this.x, (float) this.y, (float) this.z);
        buffer.writeByte((byte) (this.pitch / (360d / 256d)));
        buffer.writeByte((byte) (this.headYaw / (360d / 256d)));
        buffer.writeByte((byte) (this.yaw / (360d / 256d)));
    }
}
