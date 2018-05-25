package com.nukkitx.server.level.biome.impl;

import com.nukkitx.api.block.BlockTypes;
import com.nukkitx.server.block.NukkitBlockState;
import com.nukkitx.server.level.biome.BaseCoveredBiome;

/**
 * @author DaPorkchop_
 */
public class PlainsBiomeImpl extends BaseCoveredBiome {
    public PlainsBiomeImpl() {
        super(70,
                74,
                new NukkitBlockState(BlockTypes.DIRT, null, null),
                new NukkitBlockState(BlockTypes.GRASS_BLOCK, null, null),
                null,
                4);
    }
}
