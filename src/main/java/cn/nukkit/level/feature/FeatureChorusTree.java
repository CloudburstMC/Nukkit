package cn.nukkit.level.feature;

import cn.nukkit.block.BlockIds;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.generator.standard.misc.IntRange;
import cn.nukkit.level.generator.standard.misc.filter.BlockFilter;
import cn.nukkit.math.BlockFace;
import lombok.NonNull;
import net.daporkchop.lib.common.util.PValidation;
import net.daporkchop.lib.random.PRandom;

import static java.lang.Math.*;

/**
 * Generates a fully grown chorus tree.
 *
 * @author DaPorkchop_
 */
public class FeatureChorusTree extends ReplacingWorldFeature {
    public static final IntRange DEFAULT_BRANCH_HEIGHT = new IntRange(1, 4);
    public static final int DEFAULT_MAX_RECURSION = 4;
    public static final int DEFAULT_MAX_OVERHANG = 8;

    protected final IntRange branchHeight;
    protected final int maxRecursion;
    protected final int maxOverhang;

    public FeatureChorusTree(@NonNull IntRange branchHeight, int maxRecursion, int maxOverhang) {
        this.branchHeight = branchHeight;
        this.maxRecursion = PValidation.ensurePositive(maxRecursion);
        this.maxOverhang = PValidation.ensurePositive(maxOverhang);
    }

    @Override
    public boolean place(ChunkManager level, PRandom random, int x, int y, int z) {
        if (this.test(level.getBlockRuntimeIdUnsafe(x, y, z, 0)) && this.place0(level, random, x, y, z, 0, 0, 0)) {
            level.setBlockId(x, y, z, 0, BlockIds.CHORUS_PLANT);
            return true;
        } else {
            return false;
        }
    }

    private boolean place0(ChunkManager level, PRandom random, int x, int y, int z, int depth, int deltaX, int deltaZ) {
        final int branchHeight = this.branchHeight.rand(random) + (depth == 0 ? 1 : 0);

        for (int dy = 1; dy <= branchHeight; dy++) {
            if (!this.allNeighborsMatch(level, x, y + dy, z, BlockFilter.AIR)) {
                return false;
            }
        }

        for (int dy = 1; dy <= branchHeight; dy++) {
            level.setBlockId(x, y + dy, z, 0, BlockIds.CHORUS_PLANT);
        }

        boolean generatedBranch = false;
        y += branchHeight;
        if (depth < this.maxRecursion) {
            for (int i = random.nextInt(4) - (depth == 0 ? 0 : 1); i >= 0; i--) {
                final BlockFace face = BlockFace.Plane.HORIZONTAL.random(random);
                final int dx = face.getUnitVector().getX();
                final int dz = face.getUnitVector().getZ();

                if (abs(deltaX + dx) < this.maxOverhang && abs(deltaZ + dz) < this.maxOverhang
                        && level.getBlockRuntimeIdUnsafe(x + dx, y, z + dz, 0) == 0
                        && level.getBlockRuntimeIdUnsafe(x + dx, y - 1, z + dz, 0) == 0
                        && this.allNeighborsMatch(level, x + dx, y, z + dz, BlockFilter.AIR, face.getOpposite())
                        && this.place0(level, random, x + dx, y, z + dz, depth + 1, deltaX + dx, deltaZ + dz)) {
                    level.setBlockId(x + dx, y, z + dz, 0, BlockIds.CHORUS_PLANT);
                    generatedBranch = true;
                }
            }
        }

        if (!generatedBranch) {
            level.setBlockAt(x, y, z, 0, BlockIds.CHORUS_FLOWER, 5);
        }

        return true;
    }
}
