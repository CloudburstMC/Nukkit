package cn.nukkit.level.generator.standard.misc.selector;

import cn.nukkit.block.Block;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.NonNull;
import net.daporkchop.lib.random.PRandom;

/**
 * Allows selecting a block from a pool of options.
 *
 * @author DaPorkchop_
 * @see cn.nukkit.level.generator.standard.misc.ConstantBlock
 */
@JsonDeserialize(using = BlockSelectorDeserializer.class)
public interface BlockSelector {
    /**
     * Selects a random block.
     *
     * @param random the instance of {@link PRandom} to use for generating random numbers
     * @return the selected {@link Block}
     */
    Block select(@NonNull PRandom random);

    /**
     * Selects a random block.
     *
     * @param random the instance of {@link PRandom} to use for generating random numbers
     * @return the selected block's runtime ID
     */
    int selectRuntimeId(@NonNull PRandom random);
}
