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

    public float x;
    public float y;
    public float z;

    @Override
    protected void decode(ByteBuf buffer) {
        Vector3f v = Binary.readVector3f(buffer);
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
    }

    @Override
    protected void encode(ByteBuf buffer) {
        Binary.writeVector3f(buffer, this.x, this.y, this.z);
    }

    @Override
    public short pid() {
        return NETWORK_ID;
    }

}
