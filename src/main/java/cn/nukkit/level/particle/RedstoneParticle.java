package cn.nukkit.level.particle;

import cn.nukkit.math.Vector3f;

/**
 * Created on 2015/11/21 by xtypr.
 * Package cn.nukkit.level.particle in project Nukkit .
 */
public class RedstoneParticle extends GenericParticle {
    public RedstoneParticle(Vector3f pos) {
        this(pos, 1);
    }

    public RedstoneParticle(Vector3f pos, int lifetime) {
        super(pos, Particle.TYPE_REDSTONE, lifetime);
    }
}
