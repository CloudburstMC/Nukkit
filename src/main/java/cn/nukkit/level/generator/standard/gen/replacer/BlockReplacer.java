package cn.nukkit.level.generator.standard.gen.replacer;

import cn.nukkit.block.Block;
import cn.nukkit.level.generator.standard.misc.GenerationPass;
import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Replaces blocks as part of the terrain surface generation pipeline.
 *
 * @author DaPorkchop_
 */
@JsonDeserialize(using = BlockReplacerDeserializer.class)
public interface BlockReplacer extends GenerationPass {
    BlockReplacer[] EMPTY_ARRAY = new BlockReplacer[0];

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

    @Override
    Identifier getId();
}
