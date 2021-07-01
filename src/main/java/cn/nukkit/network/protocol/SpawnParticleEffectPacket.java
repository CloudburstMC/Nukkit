package cn.nukkit.network.protocol;

import cn.nukkit.math.Vector3f;
import lombok.ToString;

@ToString
public class SpawnParticleEffectPacket extends DataPacket {

    public byte dimensionId;
    public long entityUniqueId = -1
    public Vector3f position;
    public String particleName;

    @Override
    public byte pid() {
        return ProtocolInfo.SPAWN_PARTICLE_EFFECT_PACKET;
    }

    @Override
    public void decode() {
    	this.dimensionId = this.getByte();
		this.entityUniqueId = this.getEntityUniqueId();
		this.position = this.getVector3f();
		this.particleName = this.getString();
    }

    @Override
    public void encode() {
        this.reset();
        this.putByte(this.dimensionId);
		this.putEntityUniqueId(this.entityUniqueId);
		this.putVector3f(this.position);
		this.putString(this.particleName);
    }
}
