package com.nukkitx.server.level.biome.decorator;

import com.nukkitx.api.block.BlockState;
import com.nukkitx.api.level.chunk.Chunk;
import lombok.NonNull;

import java.util.Random;

/**
 * @author DaPorkchop_
 */
public abstract class BaseCoveredBiome extends BiomeDecorator {
    @NonNull
    private final BlockState ground;
    @NonNull
    private final BlockState surface;
    private final BlockState cover;

    private final int groundDepth;

    public BaseCoveredBiome(int minHeight, int maxHeight, BlockState ground, BlockState surface, BlockState cover, int groundDepth) {
        super(minHeight, maxHeight);
        this.ground = ground;
        this.surface = surface;
        this.cover = cover;
        this.groundDepth = groundDepth;
    }

    @Override
    public void decorate(Chunk chunk, Random random) {
        for (int x = 15; x >= 0; x--) {
            for (int z = 15; z >= 0; z--) {
                for (int y = 255; y >= 0; y--) {
                    if (chunk.getBlock(x, y, z).getBlockState().getBlockType().isSolid()) {
                        if (this.cover != null && y != 255) {
                            chunk.setBlock(x, y + 1, z, this.cover);
                        }
                        chunk.setBlock(x, y--, z, this.surface);
                        for (int i = 0; i < this.groundDepth; i++) {
                            chunk.setBlock(x, y--, z, this.ground);
                        }
                        return;
                    }
                }
            }
        }
    }
}
