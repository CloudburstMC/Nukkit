package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.block.Block;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.biome.Biome;
import cn.nukkit.level.biome.EnumBiome;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.populator.type.Populator;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.NukkitRandom;

public class PopulatorGlowStone extends Populator {

    @Override
    public void populate(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random, FullChunk chunk) {
        if (random.nextBoundedInt(9) < 8 || Biome.getBiome(chunk.getBiomeId(7, 7)).getId() == EnumBiome.SOULSAND_VALLEY.id) return;
        int x = NukkitMath.randomRange(random, chunkX << 4, (chunkX << 4) + 15);
        int z = NukkitMath.randomRange(random, chunkZ << 4, (chunkZ << 4) + 15);
        int y = getHighestWorkableBlock(chunk, x & 0xF, z & 0xF) + 1;
        boolean vertical = random.nextBoolean();
        if (y != -1) {
            int size = random.nextRange(3, 6);
            int count = NukkitMath.randomRange(random, 20 + (size << 1), 30 + (size << 1));
            if (vertical && size > 3) size--;
            for (int i = 0; i < count; i++) {
                int yy = y + (random.nextBoundedInt(size) - random.nextRange(3, vertical ? 5 : 3));
                if (yy <= 0) continue;
                int xx = x + (random.nextBoundedInt(size) - 3);
                int zz = z + (random.nextBoundedInt(size) - 3);
                if (chunk.getBlockId(xx & 0x0f, yy & 0x0ff, zz & 0x0f) == AIR) {
                    level.setBlockAt(xx, yy, zz, GLOWSTONE);
                }
            }
        }
    }

    private static int getHighestWorkableBlock(FullChunk chunk, int x, int z) {
        int y;
        // Start scanning a bit lower down to allow space for placing on top
        for (y = 120; y >= 0; y--) {
            int b = chunk.getBlockId(x, y, z);
            if (b == Block.AIR) {
                break;
            }
        }
        return y == 0 ? -1 : y;
    }
}
