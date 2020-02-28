package cn.nukkit.level.generator.standard.misc.layer;

import cn.nukkit.level.chunk.IChunk;
import lombok.NonNull;
import net.daporkchop.lib.random.PRandom;

/**
 * A layer of a certain number of blocks of a certain type.
 *
 * @author DaPorkchop_
 */
public interface BlockLayer {
    /**
     * @return the block ID used by this block layer
     */
    int blockId();

    /**
     * @return the size of this block layer
     */
    int size(@NonNull PRandom random);
}
