package cn.nukkit.network.protocol;

import cn.nukkit.utils.Binary;
import io.netty.buffer.ByteBuf;
import lombok.ToString;

@ToString
public class SpawnExperienceOrbPacket extends DataPacket {

    public static final short NETWORK_ID = ProtocolInfo.SPAWN_EXPERIENCE_ORB_PACKET;

    public float x;
    public float y;
    public float z;
    public int amount;

    @Override
    protected void decode(ByteBuf buffer) {

    }

    @Override
    protected void encode(ByteBuf buffer) {
        Binary.writeVector3f(buffer, this.x, this.y, this.z);
        Binary.writeUnsignedVarInt(buffer, this.amount);
    }

    @Override
    public short pid() {
        return NETWORK_ID;
    }
}
