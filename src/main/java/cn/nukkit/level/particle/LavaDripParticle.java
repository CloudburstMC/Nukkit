package cn.nukkit.level.particle;

import cn.nukkit.math.Vector3;

/**
 * Created on 2015/11/21 by xtypr.
 */
public class LavaDripParticle extends GenericParticle {
    public LavaDripParticle(Vector3 pos) {
        super(pos, Particle.TYPE_DRIP_LAVA);
    }
}
