package cn.nukkit.level.feature.tree;

import cn.nukkit.block.Block;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.feature.WorldFeature;
import cn.nukkit.level.generator.standard.misc.IntRange;
import cn.nukkit.registry.BlockRegistry;
import com.google.common.base.Preconditions;
import lombok.NonNull;
import net.daporkchop.lib.random.PRandom;

import static java.lang.Math.abs;

/**
 * @author DaPorkchop_
 */
public class FeatureNormalTree implements WorldFeature {
    public static final IntRange DEFAULT_HEIGHT = new IntRange(4, 7);

    protected final IntRange height;
    protected final Block    wood;
    protected final Block    leaves;

    public FeatureNormalTree(@NonNull Block wood, @NonNull Block leaves) {
        this(DEFAULT_HEIGHT, wood, leaves);
    }

    public FeatureNormalTree(int minHeight, int maxHeight, @NonNull Block wood, @NonNull Block leaves) {
        this(new IntRange(minHeight, maxHeight), wood, leaves);
    }

    public FeatureNormalTree(@NonNull IntRange height, @NonNull Block wood, @NonNull Block leaves) {
        this.height = height;
        this.wood = wood;
        this.leaves = leaves;
    }

    @Override
    public boolean place(ChunkManager level, PRandom random, int x, int y, int z) {
        if (y < 0 || y >= 256)  {
            return false;
        }

        //porktodo: method to obtain lock for region

        final int wood = BlockRegistry.get().getRuntimeId(this.wood);
        final int leaves = BlockRegistry.get().getRuntimeId(this.leaves);

        final int height = this.height.rand(random);

        for (int dy = 0; dy < height; dy++) {
            int id;
            if (y + dy >= 256 || ((id = level.getBlockRuntimeIdUnsafe(x, y + dy, z, 0)) != 0
                    && id != leaves && !BlockRegistry.get().getBlock(id).canBeReplaced()))   {
                return false;
            }
        }

        //place leaves
        for (int yy = y + height - 3; yy <= y + height; yy++)    {
            int dy = yy - (y + height);
            int radius = 1 - (dy / 2);
            for (int dx = -radius; dx <= radius; dx++)   {
                for (int dz = -radius; dz <= radius; dz++)   {
                    if (abs(dx) != radius || abs(dz) != radius || random.nextBoolean() && dy != 0)  {
                        int id = level.getBlockRuntimeIdUnsafe(x + dx, yy, z + dz, 0);
                        if (id == 0 || BlockRegistry.get().getBlock(id).canBeReplaced())    {
                            level.setBlockRuntimeIdUnsafe(x + dx, yy, z + dz, 0, leaves);
                        }
                    }
                }
            }
        }

        //place logs
        for (int dy = 0; dy < height; dy++) {
            level.setBlockRuntimeIdUnsafe(x, y + dy, z, 0, wood);
        }

        return true;
    }
}
