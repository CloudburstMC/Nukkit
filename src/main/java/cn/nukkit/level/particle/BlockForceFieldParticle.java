package cn.nukkit.level.particle;

import cn.nukkit.math.Vector3;

public class BlockForceFieldParticle extends GenericParticle {
    public BlockForceFieldParticle(Vector3 pos) {
        this(pos, 0);
    }

    public BlockForceFieldParticle(Vector3 pos, int scale) {
        super(pos, Particle.TYPE_BLOCK_FORCE_FIELD);
    }
}
