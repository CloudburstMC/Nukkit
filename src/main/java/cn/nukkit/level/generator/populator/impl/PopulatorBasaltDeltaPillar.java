package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.populator.type.Populator;
import cn.nukkit.math.NukkitRandom;
import it.unimi.dsi.fastutil.ints.IntArrayList;

public class PopulatorBasaltDeltaPillar extends Populator {

    private static IntArrayList getHighestWorkableBlocks(ChunkManager level, int x, int z) {
        int y;
        IntArrayList blockYs = new IntArrayList();
        for (y = 128; y > 0; --y) {
            int b = level.getBlockIdAt(x, y, z);
            if ((b == Block.BASALT || b == Block.BLACKSTONE) && level.getBlockIdAt(x, y + 1, z) == 0) {
                blockYs.add(y + 1);
            }
        }
        return blockYs;
    }

    @Override
    public void populate(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random, FullChunk chunk) {
        int amount = random.nextBoundedInt(128) + 128;

        IntArrayList visited = new IntArrayList();
        for (int i = 0; i < amount; ++i) {
            int x = random.nextRange(chunkX << 4, (chunkX << 4) + 15);
            int z = random.nextRange(chunkZ << 4, (chunkZ << 4) + 15);
            int pos = (x >> 1) + (z << 1);
            if (visited.contains(pos)) continue;
            IntArrayList ys = getHighestWorkableBlocks(level, x, z);
            for (int y : ys) {
                if (y <= 1) continue;
                if (random.nextBoundedInt(5) == 0) continue;
                for (int randomHeight = 0; randomHeight < random.nextBoundedInt(5) + 1; randomHeight++) {
                    int placeLocation = y + randomHeight;
                    level.setBlockAt(x, placeLocation, z, BlockID.BASALT);
                }
                visited.add(pos);
            }
        }
    }
}
