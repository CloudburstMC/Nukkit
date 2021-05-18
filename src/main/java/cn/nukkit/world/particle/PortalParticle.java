package cn.nukkit.world.particle;

import cn.nukkit.math.Vector3;

/**
 * Created on 2015/11/21 by xtypr.
 * Package cn.nukkit.level.particle in project Nukkit .
 */
public class PortalParticle extends GenericParticle {
    public PortalParticle(Vector3 pos) {
        super(pos, Particle.TYPE_PORTAL);
    }
}
