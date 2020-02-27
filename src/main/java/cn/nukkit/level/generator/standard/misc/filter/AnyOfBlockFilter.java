package cn.nukkit.level.generator.standard.misc.filter;

import cn.nukkit.block.Block;
import cn.nukkit.registry.BlockRegistry;
import lombok.NonNull;

import java.util.Arrays;

/**
 * Implementation of {@link BlockFilter} which checks if the block is contained in a list of IDs.
 *
 * @author DaPorkchop_
 */
public final class AnyOfBlockFilter implements BlockFilter {
    private final int[] runtimeIds;

    public AnyOfBlockFilter(@NonNull int[] runtimeIds) {
        Arrays.sort(this.runtimeIds = runtimeIds.clone());
    }

    @Override
    public boolean test(Block block) {
        return this.test(BlockRegistry.get().getRuntimeId(block));
    }

    @Override
    public boolean test(int runtimeId) {
        return Arrays.binarySearch(this.runtimeIds, runtimeId) >= 0;
    }
}
