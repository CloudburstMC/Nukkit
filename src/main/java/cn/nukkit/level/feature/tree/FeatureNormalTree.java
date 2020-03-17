package cn.nukkit.level.feature.tree;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.generator.standard.misc.IntRange;
import cn.nukkit.level.generator.standard.misc.selector.BlockSelector;
import lombok.NonNull;
import net.daporkchop.lib.random.PRandom;

import static java.lang.Math.*;

/**
 * Generates a normal, vanilla-style tree.
 *
 * @author DaPorkchop_
 */
public class FeatureNormalTree extends FeatureAbstractTree {
    public static final IntRange DEFAULT_HEIGHT = new IntRange(4, 7);

    public FeatureNormalTree(@NonNull IntRange height, @NonNull TreeSpecies species) {
        super(height, species);
    }

    public FeatureNormalTree(@NonNull IntRange height, @NonNull BlockSelector wood, @NonNull BlockSelector leaves) {
        super(height, wood, leaves);
    }

    @Override
    public boolean place(ChunkManager level, PRandom random, int x, int y, int z) {
        if (y < 0 || y >= 256) {
            return false;
        }

        final int log = this.log.selectRuntimeId(random);
        final int leaves = this.leaves.selectRuntimeId(random);

        final int height = this.height.rand(random);

        for (int dy = 0; dy < height; dy++) {
            if (y + dy >= 256 || !this.test(level.getBlockRuntimeIdUnsafe(x, y + dy, z, 0))) {
                return false;
            }
        }

        //place leaves
        for (int yy = y + height - 3; yy <= y + height; yy++) {
            int dy = yy - (y + height);
            int radius = 1 - (dy / 2);
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if ((abs(dx) != radius || abs(dz) != radius || random.nextBoolean() && dy != 0)
                            && this.test(level.getBlockRuntimeIdUnsafe(x + dx, yy, z + dz, 0))) {
                        level.setBlockRuntimeIdUnsafe(x + dx, yy, z + dz, 0, leaves);
                    }
                }
            }
        }

        //place logs
        for (int dy = 0; dy < height; dy++) {
            level.setBlockRuntimeIdUnsafe(x, y + dy, z, 0, log);
        }

        this.maybeReplaceBelowWithDirt(level, x, y - 1, z);

        return true;
    }
}
