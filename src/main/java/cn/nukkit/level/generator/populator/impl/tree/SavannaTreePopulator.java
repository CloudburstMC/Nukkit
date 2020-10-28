package cn.nukkit.level.generator.populator.impl.tree;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockSapling;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.object.BlockList;
import cn.nukkit.level.generator.object.tree.ObjectSavannaTree;
import cn.nukkit.level.generator.populator.type.Populator;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.NukkitRandom;

public class SavannaTreePopulator extends Populator {

    private ChunkManager level;
    private int randomAmount;
    private int baseAmount;

    private final int type;

    public SavannaTreePopulator() {
        this(BlockSapling.ACACIA);
    }

    public SavannaTreePopulator(int type) {
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
        BlockVector3 v = new BlockVector3();

        for (int i = 0; i < amount; ++i) {
            int x = NukkitMath.randomRange(random, chunkX << 4, (chunkX << 4) + 15);
            int z = NukkitMath.randomRange(random, chunkZ << 4, (chunkZ << 4) + 15);
            int y = this.getHighestWorkableBlock(x, z);
            if (y == -1) {
                continue;
            }
            new ObjectSavannaTree().generate(level, new BlockList(level), random, v.setComponents(x, y, z));
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
