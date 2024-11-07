package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.block.Block;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.populator.type.Populator;
import cn.nukkit.math.NukkitRandom;
import it.unimi.dsi.fastutil.ints.IntArrayList;

public class PopulatorWeepingVines extends Populator {

    private static int getHighestEndingBlock(ChunkManager level, int x, int y, int z) {
        for (; y > 0; --y) {
            int b = level.getBlockIdAt(x, y, z);
            int above = level.getBlockIdAt(x, y + 1, z);
            if (above == 0 && (
                    b == NETHERRACK || b == CRIMSON_NYLIUM || b == BLOCK_NETHER_WART_BLOCK ||
                            b == STILL_LAVA || b == LAVA ||
                            b == CRIMSON_FUNGUS || b == CRIMSON_ROOTS ||
                            b == QUARTZ_ORE || b == NETHER_GOLD_ORE || b == ANCIENT_DEBRIS)) {
                break;
            }
        }

        return ++y;
    }

    private static IntArrayList getHighestWorkableBlocks(ChunkManager level, int x, int z) {
        int y;
        IntArrayList blockYs = new IntArrayList();
        for (y = 128; y > 0; --y) {
            int b = level.getBlockIdAt(x, y, z);
            if ((b == Block.CRIMSON_NYLIUM || b == Block.NETHERRACK) && level.getBlockIdAt(x, y - 1, z) == 0) {
                blockYs.add(y - 1);
            }
        }
        return blockYs;
    }

    @Override
    public void populate(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random, FullChunk chunk) {
        if (random.nextBoundedInt(8) < 7) return;

        int amount = random.nextBoundedInt(5) + 1;

        for (int i = 0; i < amount; ++i) {
            int x = random.nextRange(chunkX << 4, (chunkX << 4) + 15);
            int z = random.nextRange(chunkZ << 4, (chunkZ << 4) + 15);
            IntArrayList ys = getHighestWorkableBlocks(level, x, z);
            for (int y : ys) {
                if (y <= 1) continue;
                int endY = getHighestEndingBlock(level, x, y, z);
                int amountToDecrease = Math.min(random.nextBoundedInt(y - endY), 10);
                for (int yPos = y; yPos > y - amountToDecrease; yPos--) {
                    level.setBlockAt(x, yPos, z, WEEPING_VINES);
                }
            }
        }
    }
}
