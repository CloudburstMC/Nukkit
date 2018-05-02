package com.nukkitx.api.level.chunk;

import com.nukkitx.api.block.Block;
import com.nukkitx.api.block.BlockState;
import com.nukkitx.api.level.Level;
import com.nukkitx.api.level.data.Biome;

public interface Chunk {
    int getX();

    int getZ();

    Level getLevel();

    void setBiome(int x, int z, Biome biome);

    Block getBlock(int x, int y, int z);

    Block setBlock(int x, int y, int z, BlockState state);

    Block setBlock(int x, int y, int z, BlockState state, boolean shouldRecalculateLight);

    int getHighestLayer(int x, int z);

    byte getSkyLight(int x, int y, int z);

    byte getBlockLight(int x, int y, int z);

    ChunkSnapshot toSnapshot();
}
