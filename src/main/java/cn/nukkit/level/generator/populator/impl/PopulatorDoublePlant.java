package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockIds;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.chunk.IChunk;
import cn.nukkit.level.generator.populator.helper.EnsureCover;
import cn.nukkit.level.generator.populator.helper.EnsureGrassBelow;
import cn.nukkit.level.generator.populator.type.PopulatorSurfaceBlock;
import cn.nukkit.math.BedrockRandom;

/**
 * author: DaPorkchop_
 * Nukkit Project
 */
public class PopulatorDoublePlant extends PopulatorSurfaceBlock {
    private final Block block;

    public PopulatorDoublePlant(int type)    {
        this.block = Block.get(BlockIds.DOUBLE_PLANT, type);
    }

    @Override
    protected boolean canStay(int x, int y, int z, IChunk chunk, ChunkManager level) {
        return EnsureCover.ensureCover(x, y, z, chunk) && EnsureCover.ensureCover(x, y + 1, z, chunk) && EnsureGrassBelow.ensureGrassBelow(x, y, z, chunk);
    }

    @Override
    protected Block getBlock(int x, int z, BedrockRandom random, IChunk chunk) {
        return block;
    }

    @Override
    protected void placeBlock(int x, int y, int z, Block block, IChunk chunk, BedrockRandom random) {
        super.placeBlock(x, y, z, block, chunk, random);
        chunk.setBlock(x, y + 1, z, Block.get(block.getId(), block.getDamage() | 8));
    }
}
