package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockIds;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.chunk.IChunk;
import cn.nukkit.level.generator.populator.helper.EnsureBelow;
import cn.nukkit.level.generator.populator.helper.EnsureCover;
import cn.nukkit.level.generator.populator.type.PopulatorSurfaceBlock;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.registry.BlockRegistry;
import cn.nukkit.utils.Identifier;

import static cn.nukkit.block.BlockIds.AIR;
import static cn.nukkit.block.BlockIds.NETHERRACK;

/**
 * @author DaPorkchop_
 */
public class PopulatorGroundFire extends PopulatorSurfaceBlock {
    private static final Block FIRE = Block.get(BlockIds.FIRE);

    @Override
    protected boolean canStay(int x, int y, int z, IChunk chunk, ChunkManager level) {
        return EnsureCover.ensureCover(x, y, z, chunk) && EnsureBelow.ensureBelow(x, y, z, NETHERRACK, chunk);
    }

    @Override
    protected Block getBlock(int x, int z, NukkitRandom random, IChunk chunk) {
        return FIRE;
    }

    @Override
    protected void placeBlock(int x, int y, int z, Block block, IChunk chunk, NukkitRandom random) {
        super.placeBlock(x, y, z, block, chunk, random);
        chunk.setBlockLight(x, y, z, BlockRegistry.get().getBlock(FIRE.getId(), 0).getLightLevel());
    }

    @Override
    protected int getHighestWorkableBlock(ChunkManager level, int x, int z, IChunk chunk) {
        int y;
        for (y = 0; y <= 127; ++y) {
            Identifier b = chunk.getBlockId(x, y, z);
            if (b == AIR) {
                break;
            }
        }
        return y == 0 ? -1 : y;
    }
}
