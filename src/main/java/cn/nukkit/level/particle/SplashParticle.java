package cn.nukkit.level.particle;

import cn.nukkit.math.Vector3;

/**
 * Created on 2015/11/21 by xtypr.
 * Package cn.nukkit.level.particle in project Nukkit .
 */
public class SplashParticle extends GenericParticle {

    public SplashParticle(final Vector3 pos) {
        super(pos, Particle.TYPE_WATER_SPLASH);
    }

}
