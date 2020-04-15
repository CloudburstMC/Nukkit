package cn.nukkit.level.feature.tree;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.generator.standard.misc.IntRange;
import cn.nukkit.level.generator.standard.misc.selector.BlockSelector;
import lombok.NonNull;
import net.daporkchop.lib.random.PRandom;

import static java.lang.Math.*;

/**
 * Generates a dark oak tree.
 *
 * @author DaPorkchop_
 */
public class FeatureDarkOakTree extends FeatureHugeTree {
    public static final IntRange DEFAULT_HEIGHT = new IntRange(6, 9);

    public FeatureDarkOakTree(@NonNull IntRange height, @NonNull TreeSpecies species) {
        super(height, species);
    }

    public FeatureDarkOakTree(@NonNull IntRange height, BlockSelector log, BlockSelector leaves) {
        super(height, log, leaves);
    }

    @Override
    protected boolean canPlace(ChunkManager level, PRandom random, int x, int y, int z, int height) {
        for (int dy = 0; dy <= height + 1; dy++) {
            if (y + dy < 0 || y + dy >= 256) {
                return false;
            }
            int radius = dy == 0 ? 0 : dy >= height - 1 ? 2 : 1;
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (!this.test(level.getBlockRuntimeIdUnsafe(x + dx, y + dy, z + dz, 0))) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    protected void placeLeaves(ChunkManager level, PRandom random, int x, int y, int z, int height, int log, int leaves) {
        x += random.nextInt(3) - 1;
        z += random.nextInt(3) - 1;

        y += height;
        for (int dx = -2; dx <= 0; dx++) {
            for (int dz = -2; dz <= 0; dz++) {
                if (this.test(level.getBlockRuntimeIdUnsafe(x + dx, y - 2, z + dz, 0))) {
                    level.setBlockRuntimeIdUnsafe(x + dx, y - 2, z + dz, 0, leaves);
                }
                if (this.test(level.getBlockRuntimeIdUnsafe(x - dx + 1, y - 2, z + dz, 0))) {
                    level.setBlockRuntimeIdUnsafe(x - dx + 1, y - 2, z + dz, 0, leaves);
                }
                if (this.test(level.getBlockRuntimeIdUnsafe(x + dx, y - 2, z - dz + 1, 0))) {
                    level.setBlockRuntimeIdUnsafe(x + dx, y - 2, z - dz + 1, 0, leaves);
                }
                if (this.test(level.getBlockRuntimeIdUnsafe(x - dx + 1, y - 2, z - dz + 1, 0))) {
                    level.setBlockRuntimeIdUnsafe(x - dx + 1, y - 2, z - dz + 1, 0, leaves);
                }
                if ((dx > -2 || dz > -1) && (dx != -1 || dz != -2)) {
                    if (this.test(level.getBlockRuntimeIdUnsafe(x + dx, y, z + dz, 0))) {
                        level.setBlockRuntimeIdUnsafe(x + dx, y, z + dz, 0, leaves);
                    }
                    if (this.test(level.getBlockRuntimeIdUnsafe(x - dx + 1, y, z + dz, 0))) {
                        level.setBlockRuntimeIdUnsafe(x - dx + 1, y, z + dz, 0, leaves);
                    }
                    if (this.test(level.getBlockRuntimeIdUnsafe(x + dx, y, z - dz + 1, 0))) {
                        level.setBlockRuntimeIdUnsafe(x + dx, y, z - dz + 1, 0, leaves);
                    }
                    if (this.test(level.getBlockRuntimeIdUnsafe(x - dx + 1, y, z - dz + 1, 0))) {
                        level.setBlockRuntimeIdUnsafe(x - dx + 1, y, z - dz + 1, 0, leaves);
                    }
                }
            }
        }

        if (random.nextBoolean()) {
            if (this.test(level.getBlockRuntimeIdUnsafe(x, y + 1, z, 0))) {
                level.setBlockRuntimeIdUnsafe(x, y + 1, z, 0, leaves);
            }
            if (this.test(level.getBlockRuntimeIdUnsafe(x + 1, y + 1, z, 0))) {
                level.setBlockRuntimeIdUnsafe(x + 1, y + 1, z, 0, leaves);
            }
            if (this.test(level.getBlockRuntimeIdUnsafe(x, y + 1, z + 1, 0))) {
                level.setBlockRuntimeIdUnsafe(x, y + 1, z + 1, 0, leaves);
            }
            if (this.test(level.getBlockRuntimeIdUnsafe(x + 1, y + 1, z + 1, 0))) {
                level.setBlockRuntimeIdUnsafe(x + 1, y + 1, z + 1, 0, leaves);
            }
        }

        for (int dx = -3; dx <= 4; ++dx) {
            for (int dz = -3; dz <= 4; ++dz) {
                if ((dx != -3 || dz != -3) && (dx != -3 || dz != 4) && (dx != 4 || dz != -3) && (dx != 4 || dz != 4) && (abs(dx) < 3 || abs(dz) < 3)
                        && this.test(level.getBlockRuntimeIdUnsafe(x + dx, y - 1, z + dz, 0))) {
                    level.setBlockRuntimeIdUnsafe(x + dx, y - 1, z + dz, 0, leaves);
                }
            }
        }
    }

    @Override
    protected void placeTrunk(ChunkManager level, PRandom random, int x, int y, int z, int height, int log, int leaves) {
        super.placeTrunk(level, random, x, y, z, height, log, leaves);

        //branches
        for (int dx = -1; dx <= 2; dx++) {
            for (int dz = -1; dz <= 2; dz++) {
                if (((dx == 0 || dx == 1) && (dz == 0 || dz == 1)) || random.nextInt(3) != 0) {
                    continue;
                }

                int branchSize = random.nextInt(2, 5);
                for (int dy = 0; dy < branchSize; dy++) {
                    if (this.test(level.getBlockRuntimeIdUnsafe(x + dx, y + height - dy - 2, z + dz, 0))) {
                        level.setBlockRuntimeIdUnsafe(x + dx, y + height - dy - 2, z + dz, 0, log);
                    }
                }

                for (int ddx = -2; ddx <= 2; ddx++) {
                    for (int ddz = -2; ddz <= 2; ddz++) {
                        if ((abs(ddx) != 2 && abs(ddz) != 2) && this.test(level.getBlockRuntimeIdUnsafe(x + dx + ddx, y + height, z + dz + ddz, 0))) {
                            level.setBlockRuntimeIdUnsafe(x + dx + ddx, y + height, z + dz + ddz, 0, leaves);
                        }
                        if ((abs(ddx) != 2 || abs(ddz) != 2) && this.test(level.getBlockRuntimeIdUnsafe(x + dx + ddx, y + height - 1, z + dz + ddz, 0))) {
                            level.setBlockRuntimeIdUnsafe(x + dx + ddx, y + height - 1, z + dz + ddz, 0, leaves);
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void finish(ChunkManager level, PRandom random, int x, int y, int z, int height, int log, int leaves) {
        super.finish(level, random, x, y, z, height, log, leaves);
    }
}
