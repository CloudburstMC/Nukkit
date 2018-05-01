package cn.nukkit.level.particle;

import cn.nukkit.block.Block;
import cn.nukkit.block.GlobalBlockPalette;
import cn.nukkit.math.Vector3;

/**
 * Created on 2015/11/21 by xtypr.
 * Package cn.nukkit.level.particle in project Nukkit .
 */
public class TerrainParticle extends GenericParticle {
    public TerrainParticle(Vector3 pos, Block block) {
        super(pos, Particle.TYPE_TERRAIN, GlobalBlockPalette.getOrCreateRuntimeId(block.getDamage(), block.getId()));
    }
}
