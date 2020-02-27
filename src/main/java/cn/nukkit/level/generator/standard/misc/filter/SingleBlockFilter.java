package cn.nukkit.level.generator.standard.misc.filter;

import cn.nukkit.block.Block;
import cn.nukkit.registry.BlockRegistry;
import com.google.common.base.Preconditions;
import lombok.NonNull;

/**
 * Implementation of {@link BlockFilter} which checks for equality with a single block.
 *
 * @author DaPorkchop_
 */
public final class SingleBlockFilter implements BlockFilter {
    private final Block block;
    private final int   runtimeId;

    public SingleBlockFilter(@NonNull Block block) {
        this(block, BlockRegistry.get().getRuntimeId(block));
    }

    public SingleBlockFilter(int runtimeId) {
        this(BlockRegistry.get().getBlock(runtimeId), runtimeId);
    }

    public SingleBlockFilter(@NonNull Block block, int runtimeId) {
        Preconditions.checkArgument(BlockRegistry.get().getRuntimeId(block) == runtimeId, "block does not match runtimeId");
        this.block = block;
        this.runtimeId = runtimeId;
    }

    @Override
    public boolean test(Block block) {
        return this.block.getId() == block.getId() && this.block.getDamage() == block.getDamage();
    }

    @Override
    public boolean test(int runtimeId) {
        return this.runtimeId == runtimeId;
    }
}
