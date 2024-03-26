package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.block.Block;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.populator.helper.EnsureBelow;
import cn.nukkit.level.generator.populator.helper.EnsureCover;
import cn.nukkit.level.generator.populator.type.PopulatorSurfaceBlock;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.NukkitRandom;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author DaPorkchop_
 */
public class PopulatorCactus extends PopulatorSurfaceBlock {

    private boolean checkSurroundingBlocks(int x, int y, int z, FullChunk chunk) {
        int b = chunk.getBlockId(x + BlockFace.NORTH.getXOffset() & 0xF, y, z + BlockFace.NORTH.getZOffset() & 0xF);
        if (b != Block.AIR) return false;
        b = chunk.getBlockId(x + BlockFace.EAST.getXOffset() & 0xF, y, z + BlockFace.EAST.getZOffset() & 0xF);
        if (b != Block.AIR) return false;
        b = chunk.getBlockId(x + BlockFace.SOUTH.getXOffset() & 0xF, y, z + BlockFace.SOUTH.getZOffset() & 0xF);
        if (b != Block.AIR) return false;
        b = chunk.getBlockId(x + BlockFace.WEST.getXOffset() & 0xF, y, z + BlockFace.WEST.getZOffset() & 0xF);
        return b == Block.AIR;
    }

    @Override
    protected boolean canStay(int x, int y, int z, FullChunk chunk) {
        return EnsureCover.ensureCover(x, y, z, chunk) && EnsureBelow.ensureBelow(x, y, z, SAND, chunk) && checkSurroundingBlocks(x, y, z, chunk);
    }

    @Override
    protected int getBlockId(int x, int z, NukkitRandom random, FullChunk chunk) {
        return (Block.CACTUS << Block.DATA_BITS) | 1;
    }

    @Override
    protected void placeBlock(int x, int y, int z, int id, FullChunk chunk, NukkitRandom random) {
        int height = ThreadLocalRandom.current().nextInt(3) + 1;
        if (y + height > 255) return;
        for (int i = 0; i < height; i++)    {
            chunk.setFullBlockId(x, y + i, z, id);
        }
    }
}
