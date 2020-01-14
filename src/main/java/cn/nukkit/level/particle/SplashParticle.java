package cn.nukkit.level.particle;

import cn.nukkit.math.Vector3f;

/**
 * Created on 2015/11/21 by xtypr.
 * Package cn.nukkit.level.particle in project Nukkit .
 */
public class SplashParticle extends GenericParticle {
    public SplashParticle(Vector3f pos) {
        super(pos, Particle.TYPE_WATER_SPLASH);
    }
}
