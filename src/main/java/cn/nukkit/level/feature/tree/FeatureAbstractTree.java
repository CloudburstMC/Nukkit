package cn.nukkit.level.feature.tree;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockIds;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.feature.WorldFeature;
import cn.nukkit.level.generator.standard.misc.ConstantBlock;
import cn.nukkit.level.generator.standard.misc.IntRange;
import cn.nukkit.level.generator.standard.misc.filter.BlockFilter;
import cn.nukkit.level.generator.standard.misc.selector.BlockSelector;
import cn.nukkit.registry.BlockRegistry;
import cn.nukkit.utils.Identifier;
import lombok.NonNull;

/**
 * Common code for all tree types.
 *
 * @author DaPorkchop_
 */
public abstract class FeatureAbstractTree implements WorldFeature, BlockFilter {
    protected final IntRange      height;
    protected final BlockSelector log;
    protected final BlockSelector leaves;

    public FeatureAbstractTree(@NonNull IntRange height, @NonNull TreeSpecies species) {
        this.height = height;
        this.log = new ConstantBlock(species.getLogId(), species.getLogDamage());
        this.leaves = new ConstantBlock(species.getLeavesId(), species.getLeavesDamage());
    }

    public FeatureAbstractTree(@NonNull IntRange height, @NonNull BlockSelector log, @NonNull BlockSelector leaves) {
        this.height = height;
        this.log = log;
        this.leaves = leaves;
    }

    @Override
    public boolean test(Block block) {
        Identifier id = block.getId();
        return id == BlockIds.AIR || id == BlockIds.LEAVES || id == BlockIds.LOG2 || block.canBeReplaced();
    }

    @Override
    public boolean test(int runtimeId) {
        return runtimeId == 0 || this.test(BlockRegistry.get().getBlock(runtimeId));
    }

    public void maybeReplaceBelowWithDirt(ChunkManager level, int x, int y, int z) {
        if (y >= 0 && y < 256) {
            Block below = level.getBlockAt(x, y, z);
            if (below.getId() == BlockIds.GRASS || below.getId() == BlockIds.DIRT || below.getId() == BlockIds.MYCELIUM || below.getId() == BlockIds.PODZOL) {
                level.setBlockId(x, y, z, BlockIds.DIRT);
            }
        }
    }
}
