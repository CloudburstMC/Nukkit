package cn.nukkit.level.particle;

import cn.nukkit.math.Vector3f;

/**
 * Created on 2015/11/21 by xtypr.
 * Package cn.nukkit.level.particle in project Nukkit .
 */
public class EnchantmentTableParticle extends GenericParticle {
    public EnchantmentTableParticle(Vector3f pos) {
        super(pos, Particle.TYPE_ENCHANTMENT_TABLE);
    }
}
