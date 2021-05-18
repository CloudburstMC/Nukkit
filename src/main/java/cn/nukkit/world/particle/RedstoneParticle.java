package cn.nukkit.world.particle;

import cn.nukkit.math.Vector3;

/**
 * Created on 2015/11/21 by xtypr.
 * Package cn.nukkit.level.particle in project Nukkit .
 */
public class RedstoneParticle extends GenericParticle {
    public RedstoneParticle(Vector3 pos) {
        this(pos, 1);
    }

    public RedstoneParticle(Vector3 pos, int lifetime) {
        super(pos, Particle.TYPE_REDSTONE, lifetime);
    }
}
