package cn.nukkit.server.level.generator.populator.tree;

import cn.nukkit.server.block.Block;
import cn.nukkit.server.block.BlockSapling;
import cn.nukkit.server.level.ChunkManager;
import cn.nukkit.server.level.generator.object.tree.ObjectSavannaTree;
import cn.nukkit.server.level.generator.populator.Populator;
import cn.nukkit.server.math.NukkitMath;
import cn.nukkit.server.math.NukkitRandom;
import cn.nukkit.server.math.Vector3;

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
    public void populate(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random) {
        this.level = level;
        int amount = random.nextBoundedInt(this.randomAmount + 1) + this.baseAmount;
        Vector3 v = new Vector3();

        for (int i = 0; i < amount; ++i) {
            int x = NukkitMath.randomRange(random, chunkX << 4, (chunkX << 4) + 15);
            int z = NukkitMath.randomRange(random, chunkZ << 4, (chunkZ << 4) + 15);
            int y = this.getHighestWorkableBlock(x, z);
            if (y == -1) {
                continue;
            }
            new ObjectSavannaTree().generate(level, random, v.setComponents(x, y, z));
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
