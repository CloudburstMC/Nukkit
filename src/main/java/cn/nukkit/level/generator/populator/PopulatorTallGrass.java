package cn.nukkit.level.generator.populator;

import cn.nukkit.block.Block;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.NukkitRandom;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class PopulatorTallGrass extends Populator {
    private ChunkManager level;
    private int randomAmount;
    private int baseAmount;

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
            int x = NukkitMath.randomRange(random, chunkX * 16, chunkX * 16 + 15);
            int z = NukkitMath.randomRange(random, chunkZ * 16, chunkZ * 16 + 15);
            int y = this.getHighestWorkableBlock(x, z);

            if (y != -1 && this.canTallGrassStay(x, y, z)) {
                this.level.setBlockIdAt(x, y, z, Block.DOUBLE_PLANT);
                this.level.setBlockDataAt(x, y, z, 2);
                this.level.setBlockIdAt(x, y + 1, z, Block.DOUBLE_PLANT);
                this.level.setBlockDataAt(x, y + 1, z, 10);
            }
        }
    }

    private boolean canTallGrassStay(int x, int y, int z) {
        int b = this.level.getBlockIdAt(x, y, z);
        return (b == Block.AIR || b == Block.SNOW_LAYER) && this.level.getBlockIdAt(x, y - 1, z) == Block.GRASS && this.level.getBlockIdAt(x, y + 1, z) == Block.AIR;
    }

    private int getHighestWorkableBlock(int x, int z) {
        int y;
        for (y = 127; y >= 0; --y) {
            int b = this.level.getBlockIdAt(x, y, z);
            if (b != Block.AIR && b != Block.LEAVES && b != Block.LEAVES2 && b != Block.SNOW_LAYER) {
                break;
            }
        }

        return y == 0 ? -1 : ++y;
    }
}
