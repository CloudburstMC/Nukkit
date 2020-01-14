package cn.nukkit.level.particle;

import cn.nukkit.math.Vector3f;

/**
 * Created on 2015/11/21 by xtypr.
 * Package cn.nukkit.level.particle in project Nukkit .
 */
public class InkParticle extends GenericParticle {

    public InkParticle(Vector3f pos) {
        this(pos, 0);
    }

    public InkParticle(Vector3f pos, int scale) {
        super(pos, Particle.TYPE_INK, scale);
    }
}
