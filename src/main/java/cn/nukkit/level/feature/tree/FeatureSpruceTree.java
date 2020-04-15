package cn.nukkit.level.feature.tree;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.generator.standard.misc.IntRange;
import cn.nukkit.level.generator.standard.misc.selector.BlockSelector;
import lombok.NonNull;
import net.daporkchop.lib.random.PRandom;

import static java.lang.Math.*;

/**
 * Generates a spruce tree.
 *
 * @author DaPorkchop_
 */
public class FeatureSpruceTree extends FeatureNormalTree {
    public static final IntRange DEFAULT_HEIGHT = new IntRange(6, 10);

    public FeatureSpruceTree(@NonNull IntRange height, @NonNull TreeSpecies species) {
        super(height, species);
    }

    public FeatureSpruceTree(@NonNull IntRange height, @NonNull BlockSelector wood, @NonNull BlockSelector leaves) {
        super(height, wood, leaves);
    }

    @Override
    public boolean place(ChunkManager level, PRandom random, int x, int y, int z) {
        if (y < 0 || y >= 256) {
            return false;
        }

        final int height = this.height.rand(random);
        final int leavesStart = random.nextInt(2) + 1;
        final int maxRadius = 2 + random.nextInt(2);

        for (int dy = 0; dy <= height + 1; dy++) {
            if (y + dy < 0 || y + dy >= 256) {
                return false;
            }

            int radius = dy < leavesStart ? 0 : maxRadius;
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (!this.test(level.getBlockRuntimeIdUnsafe(x, y + dy, z, 0))) {
                        return false;
                    }
                }
            }
        }

        final int log = this.log.selectRuntimeId(random);
        final int leaves = this.leaves.selectRuntimeId(random);

        //place leaves
        int radius = random.nextInt(2);
        int step = 1;
        int flag = 0;

        for (int dy = height; dy >= leavesStart; dy--) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if ((abs(dx) != radius || abs(dz) != radius || radius <= 0)
                            && this.test(level.getBlockRuntimeIdUnsafe(x + dx, y + dy, z + dz, 0))) {
                        level.setBlockRuntimeIdUnsafe(x + dx, y + dy, z + dz, 0, leaves);
                    }
                }
            }

            if (radius >= step) {
                radius = flag;
                flag = 1;

                if (++step > maxRadius) {
                    step = maxRadius;
                }
            } else {
                radius++;
            }
        }

        //place logs
        for (int dy = 0; dy < height - 1; dy++) {
            level.setBlockRuntimeIdUnsafe(x, y + dy, z, 0, log);
        }

        this.replaceGrassWithDirt(level, x, y - 1, z);

        return true;
    }
}
