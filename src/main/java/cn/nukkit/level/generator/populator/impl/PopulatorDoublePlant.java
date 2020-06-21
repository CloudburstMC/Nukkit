package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.block.BlockID;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.populator.helper.EnsureCover;
import cn.nukkit.level.generator.populator.helper.EnsureGrassBelow;
import cn.nukkit.level.generator.populator.type.PopulatorSurfaceBlock;
import cn.nukkit.math.NukkitRandom;

/**
 * author: DaPorkchop_
 * Nukkit Project
 */
public class PopulatorDoublePlant extends PopulatorSurfaceBlock {

    private final int type;

    public PopulatorDoublePlant(final int type) {
        this.type = type;
    }

    @Override
    protected boolean canStay(final int x, final int y, final int z, final FullChunk chunk) {
        return EnsureCover.ensureCover(x, y, z, chunk) && EnsureCover.ensureCover(x, y + 1, z, chunk) && EnsureGrassBelow.ensureGrassBelow(x, y, z, chunk);
    }

    @Override
    protected int getBlockId(final int x, final int z, final NukkitRandom random, final FullChunk chunk) {
        return BlockID.DOUBLE_PLANT << 4 | this.type;
    }

    @Override
    protected void placeBlock(final int x, final int y, final int z, final int id, final FullChunk chunk, final NukkitRandom random) {
        super.placeBlock(x, y, z, id, chunk, random);
        chunk.setFullBlockId(x, y + 1, z, 8 | id);
    }

}
