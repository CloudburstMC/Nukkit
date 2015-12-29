package cn.nukkit.level.particle;

import cn.nukkit.math.Vector3;

/**
 * Created on 2015/11/21 by xtypr.
 * Package cn.nukkit.level.particle in project Nukkit .
 */
public class EntityFlameParticle extends GenericParticle {
    public EntityFlameParticle(Vector3 pos) {
        super(pos, Particle.TYPE_MOB_FLAME);
    }
}
