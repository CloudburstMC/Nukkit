package cn.nukkit.level.particle;

import cn.nukkit.math.Vector3;

/**
 * Created on 2015/11/21 by xtypr.
 */
public class WaterParticle extends GenericParticle {
    public WaterParticle(Vector3 pos) {
        super(pos, Particle.TYPE_WATER_WAKE);
    }
}
