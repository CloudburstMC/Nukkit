package cn.nukkit.network.protocol;

import cn.nukkit.math.Vector3f;
import cn.nukkit.utils.Binary;
import io.netty.buffer.ByteBuf;
import lombok.ToString;

@ToString
public class SpawnParticleEffectPacket extends DataPacket {
    public static final short NETWORK_ID = ProtocolInfo.SPAWN_PARTICLE_EFFECT_PACKET;

    public int dimensionId;
    public long uniqueEntityId = -1;
    public Vector3f position;
    public String identifier;

    @Override
    public short pid() {
        return NETWORK_ID;
    }

    @Override
    protected void decode(ByteBuf buffer) {
    }

    @Override
    protected void encode(ByteBuf buffer) {
        buffer.writeByte((byte) this.dimensionId);
        Binary.writeEntityUniqueId(buffer, uniqueEntityId);
        Binary.writeVector3f(buffer, this.position);
        Binary.writeString(buffer, this.identifier);
    }
}
