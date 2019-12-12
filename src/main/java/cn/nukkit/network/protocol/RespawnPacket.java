package cn.nukkit.network.protocol;

import cn.nukkit.math.Vector3f;
import cn.nukkit.utils.Binary;
import io.netty.buffer.ByteBuf;
import lombok.ToString;

/**
 * @author Nukkit Project Team
 */
@ToString
public class RespawnPacket extends DataPacket {

    public static final short NETWORK_ID = ProtocolInfo.RESPAWN_PACKET;

    public static final int STATE_SEARCHING_FOR_SPAWN = 0;
    public static final int STATE_READY_TO_SPAWN = 1;
    public static final int STATE_CLIENT_READY_TO_SPAWN = 2;

    public float x;
    public float y;
    public float z;
    public int respawnState = STATE_SEARCHING_FOR_SPAWN;
    public long runtimeEntityId;

    @Override
    protected void decode(ByteBuf buffer) {
        Vector3f v = Binary.readVector3f(buffer);
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        this.respawnState = buffer.readByte();
        this.runtimeEntityId = Binary.readEntityRuntimeId(buffer);
    }

    @Override
    protected void encode(ByteBuf buffer) {
        Binary.writeVector3f(buffer, this.x, this.y, this.z);
        buffer.writeByte(respawnState);
        Binary.writeEntityRuntimeId(buffer, runtimeEntityId);
    }

    @Override
    public short pid() {
        return NETWORK_ID;
    }

}
