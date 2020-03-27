package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockIds;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.chunk.IChunk;
import cn.nukkit.level.generator.populator.helper.EnsureBelow;
import cn.nukkit.level.generator.populator.helper.EnsureCover;
import cn.nukkit.level.generator.populator.type.PopulatorSurfaceBlock;
import cn.nukkit.math.BedrockRandom;

import static cn.nukkit.block.BlockIds.SAND;

/**
 * @author DaPorkchop_
 */
public class PopulatorCactus extends PopulatorSurfaceBlock {
    private static final Block CACTUS = Block.get(BlockIds.CACTUS, 1);

    @Override
    protected boolean canStay(int x, int y, int z, IChunk chunk, ChunkManager level) {
        return EnsureCover.ensureCover(x, y, z, chunk) && EnsureBelow.ensureBelow(x, y, z, SAND, chunk);
    }

    @Override
    protected Block getBlock(int x, int z, BedrockRandom random, IChunk chunk) {
        return CACTUS;
    }
}
