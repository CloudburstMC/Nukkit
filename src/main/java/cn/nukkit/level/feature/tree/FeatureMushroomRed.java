package cn.nukkit.level.feature.tree;

import cn.nukkit.block.BlockHugeMushroomBrown;
import cn.nukkit.block.BlockHugeMushroomRed;
import cn.nukkit.block.BlockIds;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.generator.standard.misc.IntRange;
import lombok.NonNull;
import net.daporkchop.lib.random.PRandom;

/**
 * Generates a huge red mushroom.
 *
 * @author DaPorkchop_
 */
public class FeatureMushroomRed extends FeatureAbstractTree {
    public FeatureMushroomRed(@NonNull IntRange height) {
        super(height, null, null);
    }

    @Override
    public boolean place(ChunkManager level, PRandom random, int x, int y, int z) {
        if (y < 0 || y >= 256) {
            return false;
        }

        int height = this.height.rand(random);
        if (random.nextInt(12) == 0) {
            height <<= 1;
        }

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

        //place leaves
        //as ugly as it is, this makes more sense to hardcode than trying to be smart about it
        int yy = y + height - 3;
        this.placeSideColumn(level, x + 2, yy, z, BlockHugeMushroomRed.TOP_E);
        this.placeSideColumn(level, x + 2, yy, z + 1, BlockHugeMushroomRed.TOP_SE);
        this.placeSideColumn(level, x + 2, yy, z - 1, BlockHugeMushroomRed.TOP_NE);
        this.placeSideColumn(level, x - 2, yy, z, BlockHugeMushroomRed.TOP_W);
        this.placeSideColumn(level, x - 2, yy, z + 1, BlockHugeMushroomRed.TOP_SW);
        this.placeSideColumn(level, x - 2, yy, z - 1, BlockHugeMushroomRed.TOP_NW);
        this.placeSideColumn(level, x, yy, z + 2, BlockHugeMushroomRed.TOP_S);
        this.placeSideColumn(level, x + 1, yy, z + 2, BlockHugeMushroomRed.TOP_SE);
        this.placeSideColumn(level, x - 1, yy, z + 2, BlockHugeMushroomRed.TOP_SW);
        this.placeSideColumn(level, x, yy, z - 2, BlockHugeMushroomRed.TOP_N);
        this.placeSideColumn(level, x + 1, yy, z - 2, BlockHugeMushroomRed.TOP_NE);
        this.placeSideColumn(level, x - 1, yy, z - 2, BlockHugeMushroomRed.TOP_NW);

        yy = y + height;
        level.setBlockAt(x, yy, z, BlockIds.RED_MUSHROOM_BLOCK, BlockHugeMushroomRed.TOP);
        level.setBlockAt(x + 1, yy, z, BlockIds.RED_MUSHROOM_BLOCK, BlockHugeMushroomRed.TOP_E);
        level.setBlockAt(x + 1, yy, z + 1, BlockIds.RED_MUSHROOM_BLOCK, BlockHugeMushroomRed.TOP_SE);
        level.setBlockAt(x, yy, z + 1, BlockIds.RED_MUSHROOM_BLOCK, BlockHugeMushroomRed.TOP_S);
        level.setBlockAt(x - 1, yy, z + 1, BlockIds.RED_MUSHROOM_BLOCK, BlockHugeMushroomRed.TOP_SW);
        level.setBlockAt(x - 1, yy, z, BlockIds.RED_MUSHROOM_BLOCK, BlockHugeMushroomRed.TOP_W);
        level.setBlockAt(x - 1, yy, z - 1, BlockIds.RED_MUSHROOM_BLOCK, BlockHugeMushroomRed.TOP_NW);
        level.setBlockAt(x, yy, z - 1, BlockIds.RED_MUSHROOM_BLOCK, BlockHugeMushroomRed.TOP_N);
        level.setBlockAt(x + 1, yy, z - 1, BlockIds.RED_MUSHROOM_BLOCK, BlockHugeMushroomRed.TOP_NE);

        //place logs
        for (int dy = 0; dy < height; dy++) {
            level.setBlockAt(x, y + dy, z, 0, BlockIds.RED_MUSHROOM_BLOCK, BlockHugeMushroomRed.STEM);
        }

        this.maybeReplaceBelowWithDirt(level, x, y - 1, z);

        return true;
    }

    protected void placeSideColumn(ChunkManager level, int x, int y, int z, int damage) {
        for (int dy = 0; dy < 3; dy++) {
            level.setBlockAt(x, y + dy, z, BlockIds.RED_MUSHROOM_BLOCK, damage);
        }
    }
}
