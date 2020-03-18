package cn.nukkit.level.generator.standard.misc.filter;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockIds;
import cn.nukkit.registry.BlockRegistry;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.function.Predicate;

/**
 * Checks if a given block type is valid. Used e.g. to see if a given block may be replaced.
 *
 * @author DaPorkchop_
 */
@JsonDeserialize(using = BlockFilterDeserializer.class)
public interface BlockFilter extends Predicate<Block> {
    BlockFilter AIR = new BlockFilter() {
        @Override
        public boolean test(Block block) {
            return block.getId() == BlockIds.AIR;
        }

        @Override
        public boolean test(int runtimeId) {
            return runtimeId == 0;
        }
    };

    BlockFilter REPLACEABLE = new BlockFilter() {
        @Override
        public boolean test(Block block) {
            return block.canBeReplaced();
        }

        @Override
        public boolean test(int runtimeId) {
            return runtimeId == 0 || this.test(BlockRegistry.get().getBlock(runtimeId));
        }
    };

    @Override
    boolean test(Block block);

    /**
     * Checks if the given runtime ID matches this filter.
     *
     * @param runtimeId the runtime ID to check
     * @return whether or not the given runtime ID matches this filter
     */
    boolean test(int runtimeId);
}
