package cn.nukkit.network.protocol;

import cn.nukkit.math.Vector3f;
import lombok.ToString;

@ToString
public class SpawnParticleEffectPacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.SPAWN_PARTICLE_EFFECT_PACKET;

    public int dimensionId;
    public long uniqueEntityId = -1;
    public Vector3f position;
    public String identifier;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
    }

    @Override
    public void encode() {
        this.reset();
        this.putByte((byte) this.dimensionId);
        this.putEntityUniqueId(uniqueEntityId);
        this.putVector3f(this.position);
        this.putString(this.identifier);
        this.putBoolean(false);
    }
}
