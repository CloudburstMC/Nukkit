package com.nukkitx.server.level.biome.decorator.impl.type;

import com.nukkitx.api.block.BlockTypes;
import com.nukkitx.server.block.NukkitBlockState;
import com.nukkitx.server.level.biome.decorator.BaseCoveredBiome;

/**
 * @author DaPorkchop_
 */
public class GrassyBiome extends BaseCoveredBiome {
    public GrassyBiome()    {
        super(70,
                74,
                new NukkitBlockState(BlockTypes.DIRT, null, null),
                new NukkitBlockState(BlockTypes.GRASS_BLOCK, null, null),
                null,
                4);
    }
}
