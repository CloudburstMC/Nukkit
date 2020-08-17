package cn.nukkit.level.particle;

import cn.nukkit.math.Vector3;

/**
 * @author xtypr
 * @since 2015/11/21
 */
public class FlameParticle extends GenericParticle {
    public FlameParticle(Vector3 pos) {
        super(pos, Particle.TYPE_FLAME);
    }
}
