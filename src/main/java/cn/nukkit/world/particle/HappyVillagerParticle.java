package cn.nukkit.world.particle;

import cn.nukkit.math.Vector3;

/**
 * Created on 2015/11/21 by xtypr.
 * Package cn.nukkit.level.particle in project Nukkit .
 */
public class HappyVillagerParticle extends GenericParticle {
    public HappyVillagerParticle(Vector3 pos) {
        super(pos, Particle.TYPE_VILLAGER_HAPPY);
    }
}
