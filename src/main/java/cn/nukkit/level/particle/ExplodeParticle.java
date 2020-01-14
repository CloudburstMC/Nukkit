package cn.nukkit.level.particle;

import cn.nukkit.math.Vector3f;

/**
 * Created on 2015/11/21 by xtypr.
 * Package cn.nukkit.level.particle in project Nukkit .
 */
public class ExplodeParticle extends GenericParticle {
    public ExplodeParticle(Vector3f pos) {
        super(pos, Particle.TYPE_EXPLODE);
    }
}
