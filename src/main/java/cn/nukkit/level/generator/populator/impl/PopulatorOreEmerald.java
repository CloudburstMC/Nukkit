package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.block.Block;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.populator.type.Populator;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.NukkitRandom;

import static cn.nukkit.block.BlockID.STONE;

/**
 * @author good777LUCKY
 */
public class PopulatorOreEmerald extends Populator {

    @Override
    public void populate(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random, FullChunk chunk) {
        int sourceX = chunkX << 4;
        int sourceZ = chunkZ << 4;
        int endX = sourceX + 15;
        int endZ = sourceZ + 15;
        
        for (int i = 0; i < 11; i++) {
            int x = NukkitMath.randomRange(random, sourceX, endX);
            int z = NukkitMath.randomRange(random, sourceZ, endZ);
            int y = NukkitMath.randomRange(random, 4, 31);
            
            if (level.getBlockIdAt(x, y, z) != STONE) {
                continue;
            }
            level.setBlockAt(x, y, z, Block.EMERALD_ORE);
        }
    }
}
