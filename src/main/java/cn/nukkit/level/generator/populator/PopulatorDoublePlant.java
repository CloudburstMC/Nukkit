package cn.nukkit.level.generator.populator;

import cn.nukkit.block.Block;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.NukkitRandom;

import static cn.nukkit.block.BlockDoublePlant.TOP_HALF_BITMASK;
import static cn.nukkit.block.BlockID.DOUBLE_PLANT;

/**
 * author: DaPorkchop_
 * Nukkit Project
 */
public class PopulatorDoublePlant extends Populator {
    private ChunkManager level;
    private int randomAmount;
    private int baseAmount;
    private int type;

    public PopulatorDoublePlant(int type)    {
        this.type = type;
    }

    public void setRandomAmount(int randomAmount) {
        this.randomAmount = randomAmount;
    }

    public void setBaseAmount(int baseAmount) {
        this.baseAmount = baseAmount;
    }

    @Override
    public void populate(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random, FullChunk chunk) {
        this.level = level;
        int amount = random.nextBoundedInt(this.randomAmount + 1) + this.baseAmount;
        for (int i = 0; i < amount; ++i) {
            int x = NukkitMath.randomRange(random, chunkX * 16, chunkX * 16 + 15);
            int z = NukkitMath.randomRange(random, chunkZ * 16, chunkZ * 16 + 15);
            int y = this.getHighestWorkableBlock(x, z);

            if (y != -1 && this.canTallGrassStay(x, y, z)) {
                this.level.setBlockFullIdAt(x, y, z, (DOUBLE_PLANT << 4) | type);
                this.level.setBlockFullIdAt(x, y + 1, z, (DOUBLE_PLANT << 4) | (TOP_HALF_BITMASK | type));
            }
        }
    }

    private boolean canTallGrassStay(int x, int y, int z) {
        int b = this.level.getBlockIdAt(x, y, z);
        return (b == Block.AIR || b == Block.SNOW_LAYER) && this.level.getBlockIdAt(x, y - 1, z) == Block.GRASS && this.level.getBlockIdAt(x, y + 1, z) == Block.AIR;
    }

    private int getHighestWorkableBlock(int x, int z) {
        int y;
        for (y = 255; y >= 0; --y) {
            int b = this.level.getBlockIdAt(x, y, z);
            if (b != Block.AIR && b != Block.LEAVES && b != Block.LEAVES2 && b != Block.SNOW_LAYER) {
                break;
            }
        }

        return y == 0 ? -1 : ++y;
    }
}
