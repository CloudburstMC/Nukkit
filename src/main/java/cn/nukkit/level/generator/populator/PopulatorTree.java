package cn.nukkit.level.generator.populator;

import cn.nukkit.block.Block;
import cn.nukkit.block.Sapling;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.generator.object.tree.ObjectTree;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.NukkitRandom;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class PopulatorTree extends Populator {
    private ChunkManager level;
    private int randomAmount;
    private int baseAmount;

    private int type;

    public PopulatorTree() {
        this(Sapling.OAK);
    }

    public PopulatorTree(int type) {
        this.type = type;
    }

    public void setRandomAmount(int randomAmount) {
        this.randomAmount = randomAmount;
    }

    public void setBaseAmount(int baseAmount) {
        this.baseAmount = baseAmount;
    }

    @Override
    public void populate(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random) {
        this.level = level;
        int amount = random.nextBoundedInt(this.randomAmount + 1) + this.baseAmount;
        for (int i = 0; i < amount; ++i) {
            int x = NukkitMath.randomRange(random, chunkX << 4, (chunkX << 4) + 15);
            int z = NukkitMath.randomRange(random, chunkZ << 4, (chunkZ << 4) + 15);
            int y = this.getHighestWorkableBlock(x, z);
            if (y == -1) {
                continue;
            }
            ObjectTree.growTree(this.level, x, y, z, random, this.type);
        }
    }

    private int getHighestWorkableBlock(int x, int z) {
        int y;
        for (y = 127; y > 0; --y) {
            int b = this.level.getBlockIdAt(x, y, z);
            if (b == Block.DIRT || b == Block.GRASS) {
                break;
            } else if (b != Block.AIR && b != Block.SNOW_LAYER) {
                return -1;
            }
        }

        return ++y;
    }
}
