package cn.nukkit.level.generator.standard.gen;

import cn.nukkit.block.Block;
import cn.nukkit.utils.ConfigSection;
import lombok.NonNull;
import net.daporkchop.lib.random.PRandom;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Replaces blocks as part of the terrain surface generation pipeline.
 *
 * @author DaPorkchop_
 */
@FunctionalInterface
public interface BlockReplacer {
    /**
     * Replaces the block.
     *
     * @param prev    the previously selected block. A value of {@code null} indicates air
     * @param x       the block's X coordinate
     * @param y       the block's Y coordinate
     * @param z       the block's Z coordinate
     * @param gradX   the density gradient along the X axis
     * @param gradY   the density gradient along the Y axis
     * @param gradZ   the density gradient along the Z axis
     * @param density the density value at the block position
     * @return the replaced block. A value of {@code null} indicates air
     */
    Block replace(Block prev, int x, int y, int z, double gradX, double gradY, double gradZ, double density);

    /**
     * Creates instances of {@link BlockReplacer} from their configuration.
     *
     * @author DaPorkchop_
     */
    @FunctionalInterface
    interface Factory extends BiFunction<ConfigSection, PRandom, BlockReplacer> {
        /**
         * Creates a new {@link BlockReplacer} from the given config.
         *
         * @param config the {@link ConfigSection} containing the config to use
         * @param random an instance of {@link PRandom} for generating random numbers
         * @return a new {@link BlockReplacer} with the given configuration
         */
        @Override
        BlockReplacer apply(@NonNull ConfigSection config, @NonNull PRandom random);
    }
}
