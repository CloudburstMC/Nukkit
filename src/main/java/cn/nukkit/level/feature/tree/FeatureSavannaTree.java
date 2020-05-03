package cn.nukkit.level.feature.tree;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.generator.standard.misc.IntRange;
import cn.nukkit.level.generator.standard.misc.filter.BlockFilter;
import cn.nukkit.level.generator.standard.misc.selector.BlockSelector;
import cn.nukkit.math.BlockFace;
import lombok.NonNull;
import net.daporkchop.lib.random.PRandom;

import static java.lang.Math.*;

/**
 * Generates an acacia (savanna) tree.
 *
 * @author DaPorkchop_
 */
public class FeatureSavannaTree extends FeatureNormalTree {
    public static final IntRange DEFAULT_HEIGHT = new IntRange(5, 9);

    public FeatureSavannaTree(@NonNull IntRange height, @NonNull TreeSpecies species) {
        super(height, species);
    }

    public FeatureSavannaTree(@NonNull IntRange height, @NonNull BlockSelector wood, @NonNull BlockSelector leaves) {
        super(height, wood, leaves);
    }

    @Override
    public boolean place(ChunkManager level, PRandom random, int x, int y, int z) {
        if (y < 0 || y >= 256) {
            return false;
        }

        final int height = this.chooseHeight(level, random, x, y, z);

        if (!this.canPlace(level, random, x, y, z, height)) {
            return false;
        }

        final int log = this.selectLog(level, random, x, y, z, height);
        final int leaves = this.selectLeaves(level, random, x, y, z, height);

        int bendHeight = height - random.nextInt(4) - 1;
        int bendSize = 3 - random.nextInt(3);

        int dx = 0;
        int dz = 0;

        BlockFace direction = BlockFace.Plane.HORIZONTAL.random(random);

        for (int dy = 0; dy < height; dy++) {
            if (dy >= bendHeight && bendSize > 0)   {
                dx += direction.getXOffset();
                dz += direction.getZOffset();
                bendSize--;
            }

            if (this.test(level.getBlockRuntimeIdUnsafe(x + dx, y + dy, z + dz, 0)))    {
                level.setBlockRuntimeIdUnsafe(x + dx, y + dy, z + dz, 0, log);
            }
        }
        this.placeLeaves(level, random, x + dx, y + height - 1, z + dz, height, log, leaves);

        BlockFace secondDirection = BlockFace.Plane.HORIZONTAL.random(random);
        if (direction == secondDirection)    {
            return true;
        }

        int secondBendHeight = bendHeight - random.nextInt(2) - 1;
        int secondBendSize = 1 + random.nextInt(3);
        int lastPlacedY = 0;

        dx = dz = 0;
        for (; secondBendHeight < height && secondBendSize > 0; secondBendHeight++, secondBendSize--)    {
            dx += secondDirection.getXOffset();
            dz += secondDirection.getZOffset();

            if (this.test(level.getBlockRuntimeIdUnsafe(x + dx, y + secondBendHeight, z + dz, 0)))    {
                level.setBlockRuntimeIdUnsafe(x + dx, y + secondBendHeight, z + dz, 0, log);
                lastPlacedY = y + secondBendHeight;
            }
        }
        if (lastPlacedY > 0)    {
            this.placeLeaves(level, random, x + dx, y + secondBendHeight, z + dz, height, log, leaves);
        }

        return true;
    }

    @Override
    protected boolean canPlace(ChunkManager level, PRandom random, int x, int y, int z, int height) {
        for (int dy = 0; dy <= height + 1; dy++) {
            if (y + dy < 0 || y + dy >= 256) {
                return false;
            }

            int radius = dy == 0 ? 0 : 2;
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
    protected void placeLeaves(ChunkManager level, PRandom random, int x, int y, int z, int height, int log, int leaves) {
        for (int dx = -3; dx <= 3; dx++) {
            for (int dz = -3; dz <= 3; dz++) {
                if ((abs(dx) != 3 || abs(dz) != 3) && this.test(level.getBlockRuntimeIdUnsafe(x + dx, y, z + dz, 0))) {
                    level.setBlockRuntimeIdUnsafe(x + dx, y, z + dz, 0, leaves);
                }
            }
        }

        y++;

        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                if ((abs(dx) != 2 || abs(dz) != 2) && this.test(level.getBlockRuntimeIdUnsafe(x + dx, y, z + dz, 0))) {
                    level.setBlockRuntimeIdUnsafe(x + dx, y, z + dz, 0, leaves);
                }
            }
        }
    }
}
