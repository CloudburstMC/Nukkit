package cn.nukkit.level.feature.tree;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockIds;
import cn.nukkit.block.BlockLiquid;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.feature.WorldFeature;
import cn.nukkit.level.generator.standard.misc.ConstantBlock;
import cn.nukkit.level.generator.standard.misc.IntRange;
import cn.nukkit.level.generator.standard.misc.filter.BlockFilter;
import cn.nukkit.level.generator.standard.misc.selector.BlockSelector;
import cn.nukkit.registry.BlockRegistry;
import cn.nukkit.utils.Identifier;
import lombok.NonNull;
import net.daporkchop.lib.random.PRandom;

/**
 * Common code for all tree types.
 *
 * @author DaPorkchop_
 */
public abstract class FeatureAbstractTree implements WorldFeature, BlockFilter {
    protected final IntRange      height;
    protected final BlockSelector log;
    protected final BlockSelector leaves;

    public FeatureAbstractTree(@NonNull IntRange height) {
        this(height, null, null);
    }

    public FeatureAbstractTree(@NonNull IntRange height, @NonNull TreeSpecies species) {
        this(height, new ConstantBlock(species.getLogId(), species.getLogDamage()), new ConstantBlock(species.getLeavesId(), species.getLeavesDamage()));
    }

    public FeatureAbstractTree(@NonNull IntRange height, BlockSelector log, BlockSelector leaves) {
        this.height = height;
        this.log = log;
        this.leaves = leaves;
    }

    @Override
    public boolean test(Block block) {
        Identifier id = block.getId();
        return id == BlockIds.AIR || id == BlockIds.LEAVES || id == BlockIds.LEAVES2 || (!(block instanceof BlockLiquid) && block.canBeReplaced());
    }

    @Override
    public boolean test(int runtimeId) {
        return runtimeId == 0 || this.test(BlockRegistry.get().getBlock(runtimeId));
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

        this.placeLeaves(level, random, x, y, z, height, log, leaves);
        this.placeTrunk(level, random, x, y, z, height, log, leaves);
        this.finish(level, random, x, y, z, height, log, leaves);

        return true;
    }

    protected int chooseHeight(ChunkManager level, PRandom random, int x, int y, int z) {
        return this.height.rand(random);
    }

    protected abstract boolean canPlace(ChunkManager level, PRandom random, int x, int y, int z, int height);

    protected int selectLog(ChunkManager level, PRandom random, int x, int y, int z, int height)    {
        return this.log.selectRuntimeId(random);
    }

    protected int selectLeaves(ChunkManager level, PRandom random, int x, int y, int z, int height)    {
        return this.leaves.selectRuntimeId(random);
    }

    protected abstract void placeLeaves(ChunkManager level, PRandom random, int x, int y, int z, int height, int log, int leaves);

    protected abstract void placeTrunk(ChunkManager level, PRandom random, int x, int y, int z, int height, int log, int leaves);

    protected abstract void finish(ChunkManager level, PRandom random, int x, int y, int z, int height, int log, int leaves);

    protected void maybeReplaceBelowWithDirt(ChunkManager level, int x, int y, int z) {
        if (y >= 0 && y < 256) {
            Block below = level.getBlockAt(x, y, z);
            if (below.getId() == BlockIds.GRASS || below.getId() == BlockIds.DIRT || below.getId() == BlockIds.MYCELIUM || below.getId() == BlockIds.PODZOL) {
                level.setBlockId(x, y, z, BlockIds.DIRT);
            }
        }
    }
}
