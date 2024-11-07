package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.block.Block;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.populator.helper.EnsureCover;
import cn.nukkit.level.generator.populator.helper.EnsureGrassBelow;
import cn.nukkit.level.generator.populator.type.PopulatorSurfaceBlock;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.NukkitRandom;

/**
 * @author Niall Lindsay (Niall7459)
 * <p>
 * Nukkit Project
 * </p>
 */
public class PopulatorSugarcane extends PopulatorSurfaceBlock {

    private boolean findWater(int x, int y, int z, FullChunk chunk) {
        int cx = x & 0xF;
        int cz = z & 0xF;
        int b = chunk.getBlockId(NukkitMath.clamp(cx + BlockFace.NORTH.getXOffset(), 0, 15), y, NukkitMath.clamp(cz + BlockFace.NORTH.getZOffset(), 0, 15));
        if (b == Block.WATER || b == Block.STILL_WATER) return true;
        b = chunk.getBlockId(NukkitMath.clamp(cx + BlockFace.EAST.getXOffset(), 0, 15), y, NukkitMath.clamp(cz + BlockFace.EAST.getZOffset(), 0, 15));
        if (b == Block.WATER || b == Block.STILL_WATER) return true;
        b = chunk.getBlockId(NukkitMath.clamp(cx + BlockFace.SOUTH.getXOffset(), 0, 15), y, NukkitMath.clamp(cz + BlockFace.SOUTH.getZOffset(), 0, 15));
        if (b == Block.WATER || b == Block.STILL_WATER) return true;
        b = chunk.getBlockId(NukkitMath.clamp(cx + BlockFace.WEST.getXOffset(), 0, 15), y, NukkitMath.clamp(cz + BlockFace.WEST.getZOffset(), 0, 15));
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
