package cn.nukkit.server.level.particle;

import cn.nukkit.server.block.Block;
import cn.nukkit.server.math.Vector3;

/**
 * Created on 2015/11/21 by xtypr.
 * Package cn.nukkit.server.level.particle in project Nukkit .
 */
public class TerrainParticle extends GenericParticle {
    public TerrainParticle(Vector3 pos, Block block) {
        super(pos, Particle.TYPE_TERRAIN, (block.getDamage() << 8) | block.getId());
    }
}
