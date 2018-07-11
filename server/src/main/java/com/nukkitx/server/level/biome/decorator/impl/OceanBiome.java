package com.nukkitx.server.level.biome.decorator.impl;

import com.nukkitx.api.block.BlockState;
import com.nukkitx.api.block.BlockTypes;
import com.nukkitx.api.level.chunk.Chunk;
import com.nukkitx.server.block.NukkitBlockState;
import com.nukkitx.server.level.biome.decorator.BiomeDecorator;

import java.util.Random;

/**
 * @author DaPorkchop_
 */
public class OceanBiome extends BiomeDecorator {
    private static final BlockState WATER = new NukkitBlockState(BlockTypes.STATIONARY_WATER, null, null);
    private static final BlockState GRAVEL = new NukkitBlockState(BlockTypes.GRAVEL, null, null);
    private static final BlockState DIRT = new NukkitBlockState(BlockTypes.DIRT, null, null);

    public OceanBiome() {
        super(50, 60);
    }

    @Override
    public void cover(Chunk chunk, int x, int z, Random random) {
        for (int y = 64; y >= 0; y--) {
            if (!chunk.getBlock(x, y, z).getBlockState().getBlockType().isSolid()) {
                chunk.setBlock(x, y, z, WATER);
            } else {
                chunk.setBlock(x, y--, z, GRAVEL);
                for (int i = 0; i < 4; i++) {
                    chunk.setBlock(x, y--, z, DIRT);
                }
                return;
            }
        }
    }
}
