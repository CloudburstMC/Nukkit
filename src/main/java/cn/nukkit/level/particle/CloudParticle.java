package cn.nukkit.level.particle;

import cn.nukkit.math.Vector3;

public class CloudParticle extends GenericParticle {
    public CloudParticle(Vector3 pos) {
        this(pos, 0);
    }
    public CloudParticle(Vector3 pos, int scale) { super(pos, Particle.TYPE_EVAPORATION, scale); }
}
