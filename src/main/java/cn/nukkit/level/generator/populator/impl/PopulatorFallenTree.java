package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.block.Block;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.object.tree.ObjectFallenTree;
import cn.nukkit.level.generator.populator.type.Populator;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.NukkitRandom;

public class PopulatorFallenTree extends Populator {
    
    private ChunkManager level;
    private int type;

    public void setType(int type) { //0 = oak, 1 = birch
        this.type = type;
    }

    @Override
    public void populate(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random, FullChunk chunk) {
        if (random.nextRange(0, 3) != 1) return;
        this.level = level;
        int amount = 1;
        for (int i = 0; i < amount; ++i) {
            int x = NukkitMath.randomRange(random, chunkX << 4, (chunkX << 4) + 15);
            int z = NukkitMath.randomRange(random, chunkZ << 4, (chunkZ << 4) + 15);
            int y = this.getHighestWorkableBlock(x, z);
            if (y == -1) {
                continue;
            }
            if (level.getBlockIdAt(x, y, z) != Block.AIR ||
                    level.getBlockIdAt(x + 1, y, z) != Block.AIR ||
                    level.getBlockIdAt(x, y, z + 1) != Block.AIR ||
                    level.getBlockIdAt(x - 1, y, z) != Block.AIR ||
                    level.getBlockIdAt(x, y, z - 1) != Block.AIR ||
                    level.getBlockIdAt(x + 1, y - 1, z) != Block.GRASS ||
                    level.getBlockIdAt(x, y - 1, z + 1) != Block.GRASS ||
                    level.getBlockIdAt(x - 1, y - 1, z) != Block.GRASS ||
                    level.getBlockIdAt(x, y - 1, z - 1) != Block.GRASS ||
                    level.getBlockIdAt(x + 2, y - 1, z) != Block.GRASS ||
                    level.getBlockIdAt(x, y - 1, z + 2) != Block.GRASS ||
                    level.getBlockIdAt(x - 2, y - 1, z) != Block.GRASS ||
                    level.getBlockIdAt(x, y - 1, z - 2) != Block.GRASS ||
                    level.getBlockIdAt(x - 3, y - 1, z) != Block.GRASS ||
                    level.getBlockIdAt(x, y - 1, z - 3) != Block.GRASS ||
                    level.getBlockIdAt(x + 2, y, z) != Block.AIR ||
                    level.getBlockIdAt(x, y, z + 2) != Block.AIR ||
                    level.getBlockIdAt(x + 3, y, z) != Block.AIR ||
                    level.getBlockIdAt(x, y, z + 3) != Block.AIR) {
                continue;
            }
            new ObjectFallenTree(this.level, x, y, z, type, random);
        }
    }

    private int getHighestWorkableBlock(int x, int z) {
        int y;

        for (y = 127; y > 0; --y) {
            int b = this.level.getBlockIdAt(x, y, z);
            if (b == Block.GRASS) {
                break;
            }
        }

        return ++y;
    }
}