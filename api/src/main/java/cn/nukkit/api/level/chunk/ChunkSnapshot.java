package cn.nukkit.api.level.chunk;

import cn.nukkit.api.block.BlockSnapshot;
import cn.nukkit.api.level.data.Biome;

import javax.annotation.concurrent.Immutable;

@Immutable
public interface ChunkSnapshot {
    int getX();

    int getZ();

    BlockSnapshot getBlock(int x, int y, int z);

    int getHighestLayer(int x, int z);

    byte getSkyLight(int x, int y, int z);

    byte getBlockLight(int x, int y, int z);

    Biome getBiome(int x, int z);
}