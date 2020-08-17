package cn.nukkit.level.particle;

import cn.nukkit.math.Vector3;

/**
 * @author xtypr
 * @since 2015/11/21
 */
public class BubbleParticle extends GenericParticle {
    public BubbleParticle(Vector3 pos) {
        super(pos, Particle.TYPE_BUBBLE);
    }
}
