package cn.nukkit.level.particle;

import cn.nukkit.math.Vector3;

/**
 * Created on 2015/11/21 by xtypr.
 * Package cn.nukkit.level.particle in project Nukkit .
 */
public class CriticalParticle extends GenericParticle {
    public CriticalParticle(Vector3 pos){
        this(pos, 2);
    }

    public CriticalParticle(Vector3 pos, int scale) {
        super(pos, 2, scale);
    }
}
