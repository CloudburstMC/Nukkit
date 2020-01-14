package cn.nukkit.level.particle;

import cn.nukkit.math.Vector3f;

/**
 * Created on 2015/11/21 by xtypr.
 * Package cn.nukkit.level.particle in project Nukkit .
 */
public class FlameParticle extends GenericParticle {
    public FlameParticle(Vector3f pos) {
        super(pos, Particle.TYPE_FLAME);
    }
}
