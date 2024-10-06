package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.block.Block;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.populator.type.Populator;
import cn.nukkit.math.NukkitRandom;
import it.unimi.dsi.fastutil.ints.IntArrayList;

public class PopulatorTwistingVines extends Populator {

    private static int getHighestEndingBlock(ChunkManager level, int x, int y, int z) {
        for (; y < 128; ++y) {
            int b = level.getBlockIdAt(x, y, z);
            int above = level.getBlockIdAt(x, y + 1, z);
            if (b == 0 && (
                    above == NETHERRACK || above == WARPED_NYLIUM || above == WARPED_WART_BLOCK ||
                            above == STILL_LAVA || above == LAVA ||
                            above == WARPED_FUNGUS || above == WARPED_ROOTS ||
                            above == QUARTZ_ORE || above == NETHER_GOLD_ORE || above == ANCIENT_DEBRIS)) {
                break;
            }
        }

        return y;
    }

    private static IntArrayList getHighestWorkableBlocks(ChunkManager level, int x, int z) {
        int y;
        IntArrayList blockYs = new IntArrayList();

        for (y = 128; y > 0; --y) {
            int b = level.getBlockIdAt(x, y, z);
            if ((b == Block.WARPED_NYLIUM || b == Block.NETHERRACK) && level.getBlockIdAt(x, y + 1, z) == 0) {
                blockYs.add(y + 1);
            }
        }
        return blockYs;
    }

    @Override
    public void populate(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random, FullChunk chunk) {
        if (random.nextBoundedInt(8) < 7) return;

        int amount = random.nextBoundedInt(5) + 2;

        for (int i = 0; i < amount; ++i) {
            int x = random.nextRange(chunkX << 4, (chunkX << 4) + 15);
            int z = random.nextRange(chunkZ << 4, (chunkZ << 4) + 15);
            IntArrayList ys = getHighestWorkableBlocks(level, x, z);
            for (int y : ys) {
                if (y <= 1) continue;
                if (random.nextBoundedInt(4) == 0) continue;
                int endY = getHighestEndingBlock(level, x, y, z);
                int amountToDecrease = Math.min(random.nextBoundedInt(endY - y), 15);
                for (int yPos = y; yPos < y + (amountToDecrease / 2); yPos++) {
                    level.setBlockAt(x, yPos, z, TWISTING_VINES);
                }
            }
        }
    }
}
