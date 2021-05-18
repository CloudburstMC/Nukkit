package cn.nukkit.world.particle;

import cn.nukkit.block.Block;
import cn.nukkit.math.Vector3;
import cn.nukkit.world.GlobalBlockPalette;

/**
 * Created on 2015/11/21 by xtypr.
 * Package cn.nukkit.level.particle in project Nukkit .
 */
public class TerrainParticle extends GenericParticle {
    public TerrainParticle(Vector3 pos, Block block) {
        super(pos, Particle.TYPE_TERRAIN, GlobalBlockPalette.getOrCreateRuntimeId(block.getId(), block.getDamage()));
    }
}
