package cn.nukkit.level.feature.tree;

import cn.nukkit.block.BlockIds;
import cn.nukkit.block.BlockVine;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.generator.standard.misc.IntRange;
import cn.nukkit.level.generator.standard.misc.selector.BlockSelector;
import cn.nukkit.math.BlockFace;
import cn.nukkit.registry.BlockRegistry;
import lombok.NonNull;
import net.daporkchop.lib.random.PRandom;

/**
 * Generates normal trees, but with vines on the sides.
 *
 * @author DaPorkchop_
 */
public class FeatureSwampTree extends FeatureNormalTree {
    public static final IntRange DEFAULT_HEIGHT = new IntRange(5, 8);

    public FeatureSwampTree(@NonNull IntRange height, @NonNull TreeSpecies species) {
        super(height, species);
    }

    public FeatureSwampTree(@NonNull IntRange height, @NonNull BlockSelector wood, @NonNull BlockSelector leaves) {
        super(height, wood, leaves);
    }

    @Override
    protected void finish(ChunkManager level, PRandom random, int x, int y, int z, int height, int log, int leaves) {
        super.finish(level, random, x, y, z, height, log, leaves);

        y = y + height - 3;
        for (int dx = -3; dx <= 3; dx++)    {
            for (int dz = -3; dz <= 3; dz++)    {
                if (!this.test(level.getBlockRuntimeIdUnsafe(x + dx, y, z + dz, 0)) || random.nextInt(4) != 0)    {
                    continue;
                }

                if (level.getBlockRuntimeIdUnsafe(x + dx + 1, y, z + dz, 0) == leaves)  {
                    this.placeVines(level, random,x + dx, y, z + dz, BlockFace.WEST, leaves);
                } else if (level.getBlockRuntimeIdUnsafe(x + dx - 1, y, z + dz, 0) == leaves)  {
                    this.placeVines(level, random,x + dx, y, z + dz, BlockFace.EAST, leaves);
                } else if (level.getBlockRuntimeIdUnsafe(x + dx, y, z + dz + 1, 0) == leaves)  {
                    this.placeVines(level, random,x + dx, y, z + dz, BlockFace.NORTH, leaves);
                } else if (level.getBlockRuntimeIdUnsafe(x + dx, y, z + dz - 1, 0) == leaves)  {
                    this.placeVines(level, random,x + dx, y, z + dz, BlockFace.SOUTH, leaves);
                }
            }
        }
    }

    protected void placeVines(ChunkManager level, PRandom random, int x, int y, int z, BlockFace face, int leaves)  {
        int block = BlockRegistry.get().getRuntimeId(BlockIds.VINE, BlockVine.getMeta(face.getOpposite()));
        for (int dy = 0, id; dy < 4 && (id = level.getBlockRuntimeIdUnsafe(x, y - dy, z, 0)) != leaves && this.test(id); dy++)  {
            level.setBlockRuntimeIdUnsafe(x, y - dy, z, 0, block);
        }
    }
}
