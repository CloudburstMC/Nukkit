package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.block.Block;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.populator.helper.EnsureCover;
import cn.nukkit.level.generator.populator.helper.EnsureGrassBelow;
import cn.nukkit.level.generator.populator.type.PopulatorSurfaceBlock;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.NukkitRandom;

/**
 * @author Niall Lindsay (Niall7459)
 * <p>
 * Nukkit Project
 * </p>
 */
public class PopulatorSugarcane extends PopulatorSurfaceBlock {

    private boolean findWater(int x, int y, int z, FullChunk chunk) {
        int b = chunk.getBlockId(x + BlockFace.NORTH.getXOffset() & 0xF, y, z + BlockFace.NORTH.getZOffset() & 0xF);
        if (b == Block.WATER || b == Block.STILL_WATER) return true;
        b = chunk.getBlockId(x + BlockFace.EAST.getXOffset() & 0xF, y, z + BlockFace.EAST.getZOffset() & 0xF);
        if (b == Block.WATER || b == Block.STILL_WATER) return true;
        b = chunk.getBlockId(x + BlockFace.SOUTH.getXOffset() & 0xF, y, z + BlockFace.SOUTH.getZOffset() & 0xF);
        if (b == Block.WATER || b == Block.STILL_WATER) return true;
        b = chunk.getBlockId(x + BlockFace.WEST.getXOffset() & 0xF, y, z + BlockFace.WEST.getZOffset() & 0xF);
        return b == Block.WATER || b == Block.STILL_WATER;
    }

    @Override
    protected boolean canStay(int x, int y, int z, FullChunk chunk) {
        return EnsureCover.ensureCover(x, y, z, chunk) && EnsureGrassBelow.ensureGrassOrSandBelow(x, y, z, chunk) && findWater(x, y - 1, z, chunk);
    }

    @Override
    protected int getBlockId(int x, int z, NukkitRandom random, FullChunk chunk) {
        return (Block.SUGARCANE_BLOCK << Block.DATA_BITS) | 1;
    }
}
