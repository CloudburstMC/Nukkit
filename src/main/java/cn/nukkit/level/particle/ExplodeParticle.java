package cn.nukkit.level.particle;

import cn.nukkit.math.Vector3;

/**
 * @author xtypr
 * @since 2015/11/21
 */
public class ExplodeParticle extends GenericParticle {
    public ExplodeParticle(Vector3 pos) {
        super(pos, Particle.TYPE_EXPLODE);
    }
}
