package cn.nukkit.level.generator.populator.impl.tree;

import cn.nukkit.block.BlockSapling;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.chunk.IChunk;
import cn.nukkit.level.generator.object.tree.ObjectSavannaTree;
import cn.nukkit.level.generator.populator.type.Populator;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3i;
import cn.nukkit.utils.Identifier;

import static cn.nukkit.block.BlockIds.*;

public class SavannaTreePopulator extends Populator {

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
    public void populate(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random, IChunk chunk) {
        int amount = random.nextBoundedInt(this.randomAmount + 1) + this.baseAmount;
        Vector3i v = new Vector3i();

        for (int i = 0; i < amount; ++i) {
            int x = NukkitMath.randomRange(random, chunkX << 4, (chunkX << 4) + 15);
            int z = NukkitMath.randomRange(random, chunkZ << 4, (chunkZ << 4) + 15);
            int y = this.getHighestWorkableBlock(level, x, z);
            if (y == -1) {
                continue;
            }
            new ObjectSavannaTree().generate(level, random, v.setComponents(x, y, z));
        }
    }

    private int getHighestWorkableBlock(ChunkManager level, int x, int z) {
        int y;
        for (y = 127; y > 0; --y) {
            Identifier b = level.getBlockIdAt(x, y, z);
            if (b == DIRT || b == GRASS) {
                break;
            } else if (b != AIR && b != SNOW_LAYER) {
                return -1;
            }
        }

        return ++y;
    }
}
