package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockIds;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.chunk.IChunk;
import cn.nukkit.level.generator.populator.helper.EnsureBelow;
import cn.nukkit.level.generator.populator.helper.EnsureCover;
import cn.nukkit.level.generator.populator.type.PopulatorSurfaceBlock;
import cn.nukkit.math.NukkitRandom;

import static cn.nukkit.block.BlockIds.WATER;

/**
 * @author DaPorkchop_
 */
public class PopulatorLilyPad extends PopulatorSurfaceBlock {
    private static final Block WATER_LILY = Block.get(BlockIds.WATERLILY);

    @Override
    protected boolean canStay(int x, int y, int z, IChunk chunk, ChunkManager level) {
        return EnsureCover.ensureCover(x, y, z, chunk) && EnsureBelow.ensureBelow(x, y, z, WATER, chunk);
    }

    @Override
    protected Block getBlock(int x, int z, NukkitRandom random, IChunk chunk) {
        return WATER_LILY;
    }
}
