package cn.nukkit.level.particle;

import cn.nukkit.math.Vector3f;

public class BlockForceFieldParticle extends GenericParticle {
    public BlockForceFieldParticle(Vector3f pos) {
        this(pos, 0);
    }

    public BlockForceFieldParticle(Vector3f pos, int scale) {
        super(pos, Particle.TYPE_BLOCK_FORCE_FIELD);
    }
}
