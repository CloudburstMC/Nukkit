package cn.nukkit.level.generator.standard.misc;

import cn.nukkit.block.Block;
import cn.nukkit.registry.BlockRegistry;
import com.google.common.base.Preconditions;
import lombok.NonNull;

import java.util.Arrays;
import java.util.function.Predicate;

/**
 * @author DaPorkchop_
 */
@FunctionalInterface
public interface BlockMatcher extends Predicate<Block> {
    @Override
    boolean test(Block block);

    default boolean test(int runtimeId) {
        return this.test(BlockRegistry.get().getBlock(runtimeId));
    }

    /**
     * Implementation of {@link BlockMatcher} which checks for equality with a single block.
     *
     * @author DaPorkchop_
     */
    final class Single implements BlockMatcher {
        private final Block block;
        private final int   runtimeId;

        public Single(@NonNull Block block) {
            this(block, BlockRegistry.get().getRuntimeId(block));
        }

        public Single(int runtimeId) {
            this(BlockRegistry.get().getBlock(runtimeId), runtimeId);
        }

        public Single(@NonNull Block block, int runtimeId) {
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

    /**
     * Implementation of {@link BlockMatcher} which checks for equality with list of IDs.
     *
     * @author DaPorkchop_
     */
    final class AnyOf implements BlockMatcher {
        private final int[] runtimeIds;

        public AnyOf(@NonNull int[] runtimeIds) {
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
}
