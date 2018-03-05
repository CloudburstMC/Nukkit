package cn.nukkit.api.level.chunk;

import cn.nukkit.api.block.Block;
import cn.nukkit.api.block.BlockState;
import cn.nukkit.api.level.Level;

public interface Chunk {
    int getX();

    int getZ();

    Level getLevel();

    Block getBlock(int x, int y, int z);

    Block setBlock(int x, int y, int z, BlockState state);

    Block setBlock(int x, int y, int z, BlockState state, boolean shouldRecalculateLight);

    int getHighestLayer(int x, int z);

    byte getSkyLight(int x, int y, int z);

    byte getBlockLight(int x, int y, int z);

    ChunkSnapshot toSnapshot();
}
