package cn.nukkit.level.particle;

import cn.nukkit.math.Vector3;

/**
 * Created on 2015/11/21 by xtypr.
 * Package cn.nukkit.level.particle in project Nukkit .
 */
public class CriticalParticle extends GenericParticle {

    public CriticalParticle(final Vector3 pos) {
        this(pos, 2);
    }

    public CriticalParticle(final Vector3 pos, final int scale) {
        super(pos, Particle.TYPE_CRITICAL, scale);
    }

}
