package cn.nukkit.level.generator.populator.impl.tree;

import cn.nukkit.block.*;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.object.tree.ObjectJungleBigTree;
import cn.nukkit.level.generator.populator.type.Populator;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;

public class JungleBigTreePopulator extends Populator {

    private final int type;

    private ChunkManager level;

    private int randomAmount;

    private int baseAmount;

    public JungleBigTreePopulator() {
        this(BlockSapling.JUNGLE);
    }

    public JungleBigTreePopulator(final int type) {
        this.type = type;
    }

    public void setRandomAmount(final int randomAmount) {
        this.randomAmount = randomAmount;
    }

    public void setBaseAmount(final int baseAmount) {
        this.baseAmount = baseAmount;
    }

    @Override
    public void populate(final ChunkManager level, final int chunkX, final int chunkZ, final NukkitRandom random, final FullChunk chunk) {
        this.level = level;
        final int amount = random.nextBoundedInt(this.randomAmount + 1) + this.baseAmount;
        final Vector3 v = new Vector3();

        for (int i = 0; i < amount; ++i) {
            final int x = NukkitMath.randomRange(random, chunkX << 4, (chunkX << 4) + 15);
            final int z = NukkitMath.randomRange(random, chunkZ << 4, (chunkZ << 4) + 15);
            final int y = this.getHighestWorkableBlock(x, z);
            if (y == -1) {
                continue;
            }
            new ObjectJungleBigTree(10, 20, Block.get(BlockID.WOOD, BlockWood.JUNGLE), Block.get(BlockID.LEAVES, BlockLeaves.JUNGLE)).generate(this.level, random, v.setComponents(x, y, z));
        }
    }

    private int getHighestWorkableBlock(final int x, final int z) {
        int y;
        for (y = 255; y > 0; --y) {
            final int b = this.level.getBlockIdAt(x, y, z);
            if (b == BlockID.DIRT || b == BlockID.GRASS) {
                break;
            } else if (b != BlockID.AIR && b != BlockID.SNOW_LAYER) {
                return -1;
            }
        }

        return ++y;
    }

}
