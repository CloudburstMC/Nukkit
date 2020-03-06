package cn.nukkit.level.generator.standard.misc;

import cn.nukkit.level.generator.standard.StandardGenerator;
import cn.nukkit.utils.Identifier;

/**
 * Implemented by every interface representing a single pass during world generation or population.
 *
 * @author DaPorkchop_
 */
public interface GenerationPass {
    /**
     * Prepares this instance for actual generation.
     *
     * @param levelSeed the level seed, as defined in nukkit.yml
     * @param localSeed a seed defined specifically for this generation pass
     * @param generator the instance of {@link StandardGenerator} that this generation pass will be used by
     */
    default void init(long levelSeed, long localSeed, StandardGenerator generator) {
        //no-op
    }

    /**
     * @return the identifier of this generation pass
     */
    Identifier getId();
}
