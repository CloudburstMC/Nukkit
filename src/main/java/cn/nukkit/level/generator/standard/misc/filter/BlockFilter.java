package cn.nukkit.level.generator.standard.misc.filter;

import cn.nukkit.block.Block;
import cn.nukkit.registry.BlockRegistry;

import java.util.function.Predicate;

/**
 * Checks if a given block type is valid. Used e.g. to see if a given block may be replaced.
 *
 * @author DaPorkchop_
 */
@FunctionalInterface
public interface BlockFilter extends Predicate<Block> {
    @Override
    boolean test(Block block);

    default boolean test(int runtimeId) {
        return this.test(BlockRegistry.get().getBlock(runtimeId));
    }
}
