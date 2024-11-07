package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.populator.helper.EnsureBelow;
import cn.nukkit.level.generator.populator.helper.EnsureCover;
import cn.nukkit.level.generator.populator.type.PopulatorSurfaceBlock;
import cn.nukkit.math.NukkitRandom;

import java.util.concurrent.ThreadLocalRandom;

public class PopulatorSweetBerryBush extends PopulatorSurfaceBlock {

    @Override
    protected boolean canStay(int x, int y, int z, FullChunk chunk) {
        if (chunk instanceof cn.nukkit.level.format.anvil.Chunk) return false;
        return EnsureCover.ensureCover(x, y, z, chunk) && EnsureBelow.ensureBelow(x, y, z, GRASS, chunk);
    }

    @Override
    protected int getBlockId(int x, int z, NukkitRandom random, FullChunk chunk) {
        return (BlockID.SWEET_BERRY_BUSH << Block.DATA_BITS) + ThreadLocalRandom.current().nextInt(3);
    }
}
