package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.populator.type.Populator;
import cn.nukkit.math.NukkitRandom;

import static cn.nukkit.block.BlockID.EMERALD_ORE;
import static cn.nukkit.block.BlockID.STONE;

/**
 * @author good777LUCKY
 */
public class PopulatorOreEmerald extends Populator {

    @Override
    public void populate(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random, FullChunk chunk) {
        int sourceX = chunkX << 4;
        int sourceZ = chunkZ << 4;
        
        for (int i = 0; i < 11; i++) {
            int x = sourceX + random.nextInt(16);
            int z = sourceZ + random.nextInt(16);
            int y = random.nextInt(28) + 4;
            
            if (chunk.getBlockId(x, y, z) != STONE) {
                continue;
            }
            chunk.setBlockId(x, y, z, EMERALD_ORE);
        }
    }
}
