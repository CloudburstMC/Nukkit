package cn.nukkit.level.feature.tree;

import cn.nukkit.block.BlockHugeMushroomBrown;
import cn.nukkit.block.BlockIds;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.generator.standard.misc.IntRange;
import cn.nukkit.registry.BlockRegistry;
import lombok.NonNull;
import net.daporkchop.lib.random.PRandom;

import static java.lang.Math.abs;

/**
 * Generates a huge brown mushroom.
 *
 * @author DaPorkchop_
 */
public class FeatureMushroomBrown extends FeatureAbstractTree {
    public FeatureMushroomBrown(@NonNull IntRange height) {
        super(height);
    }

    @Override
    protected int chooseHeight(ChunkManager level, PRandom random, int x, int y, int z) {
        return this.height.rand(random) << (~random.nextInt(12) & 1);
    }

    @Override
    protected boolean canPlace(ChunkManager level, PRandom random, int x, int y, int z, int height) {
        for (int dy = 0; dy <= height + 1; dy++) {
            if (y + dy < 0 ||y + dy >= 256) {
                return false;
            }

            int radius = dy < 3 ? 0 : 4;
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (!this.test(level.getBlockRuntimeIdUnsafe(x, y + dy, z, 0))) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    @Override
    protected int selectLog(ChunkManager level, PRandom random, int x, int y, int z, int height) {
        return BlockRegistry.get().getRuntimeId(BlockIds.BROWN_MUSHROOM_BLOCK, BlockHugeMushroomBrown.STEM);
    }

    @Override
    protected int selectLeaves(ChunkManager level, PRandom random, int x, int y, int z, int height) {
        return -1;
    }

    @Override
    protected void placeLeaves(ChunkManager level, PRandom random, int x, int y, int z, int height, int log, int leaves) {
        //as ugly as it is, this makes more sense to hardcode than trying to be smart about it
        int yy = y + height;
        for (int dx = -2; dx <= 2; dx++)    {
            for (int dz = -2; dz <= 2; dz++)    {
                level.setBlockAt(x + dx, yy, z + dz, BlockIds.BROWN_MUSHROOM_BLOCK, BlockHugeMushroomBrown.TOP);
            }
        }
        for (int dx = -1; dx <= 1; dx++)    {
            level.setBlockAt(x + dx, yy, z + 3, BlockIds.BROWN_MUSHROOM_BLOCK, BlockHugeMushroomBrown.TOP_S);
            level.setBlockAt(x + dx, yy, z - 3, BlockIds.BROWN_MUSHROOM_BLOCK, BlockHugeMushroomBrown.TOP_N);
        }
        for (int dz = -1; dz <= 1; dz++)    {
            level.setBlockAt(x + 3, yy, z + dz, BlockIds.BROWN_MUSHROOM_BLOCK, BlockHugeMushroomBrown.TOP_E);
            level.setBlockAt(x - 3, yy, z + dz, BlockIds.BROWN_MUSHROOM_BLOCK, BlockHugeMushroomBrown.TOP_W);
        }
        level.setBlockAt(x + 2, yy, z + 3, BlockIds.BROWN_MUSHROOM_BLOCK, BlockHugeMushroomBrown.TOP_SE);
        level.setBlockAt(x - 2, yy, z + 3, BlockIds.BROWN_MUSHROOM_BLOCK, BlockHugeMushroomBrown.TOP_SW);
        level.setBlockAt(x + 2, yy, z - 3, BlockIds.BROWN_MUSHROOM_BLOCK, BlockHugeMushroomBrown.TOP_NE);
        level.setBlockAt(x - 2, yy, z - 3, BlockIds.BROWN_MUSHROOM_BLOCK, BlockHugeMushroomBrown.TOP_NW);
        level.setBlockAt(x + 3, yy, z + 2, BlockIds.BROWN_MUSHROOM_BLOCK, BlockHugeMushroomBrown.TOP_SE);
        level.setBlockAt(x + 3, yy, z - 2, BlockIds.BROWN_MUSHROOM_BLOCK, BlockHugeMushroomBrown.TOP_NE);
        level.setBlockAt(x - 3, yy, z + 2, BlockIds.BROWN_MUSHROOM_BLOCK, BlockHugeMushroomBrown.TOP_SW);
        level.setBlockAt(x - 3, yy, z - 2, BlockIds.BROWN_MUSHROOM_BLOCK, BlockHugeMushroomBrown.TOP_NW);
    }

    @Override
    protected void placeTrunk(ChunkManager level, PRandom random, int x, int y, int z, int height, int log, int leaves) {
        for (int dy = 0; dy < height; dy++) {
            level.setBlockRuntimeIdUnsafe(x, y + dy, z, 0, log);
        }
    }

    @Override
    protected void finish(ChunkManager level, PRandom random, int x, int y, int z, int height, int log, int leaves) {
        this.replaceGrassWithDirt(level, x, y - 1, z);
    }
}
