package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockIds;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.chunk.IChunk;
import cn.nukkit.level.generator.populator.helper.EnsureBelow;
import cn.nukkit.level.generator.populator.helper.EnsureCover;
import cn.nukkit.level.generator.populator.helper.EnsureGrassBelow;
import cn.nukkit.level.generator.populator.type.PopulatorSurfaceBlock;
import cn.nukkit.math.BedrockRandom;
import cn.nukkit.utils.Identifier;

import static cn.nukkit.block.BlockIds.*;

/**
 * @author Niall Lindsay (Niall7459)
 * <p>
 * Nukkit Project
 * </p>
 */
public class PopulatorSugarcane extends PopulatorSurfaceBlock {
    private static final Block SUGARCANE_BLOCK = Block.get(BlockIds.REEDS, 1);

    private boolean findWater(int x, int y, int z, ChunkManager level) {
        int count = 0;
        for (int i = x - 4; i < (x + 4); i++) {
            for (int j = z - 4; j < (z + 4); j++) {
                Identifier b = level.getBlockId(i, y, j);
                if (b == FLOWING_WATER || b == WATER) {
                    count++;
                }
                if (count > 10) {
                    return true;
                }
            }
        }
        return (count > 10);
    }

    @Override
    protected boolean canStay(int x, int y, int z, IChunk chunk, ChunkManager level) {
        return EnsureCover.ensureCover(x, y, z, chunk) &&
                (EnsureGrassBelow.ensureGrassBelow(x, y, z, chunk) || EnsureBelow.ensureBelow(x, y, z, SAND, chunk)) &&
                findWater(x + (chunk.getX() << 4), y - 1, z + (chunk.getZ() << 4), level);
    }

    @Override
    protected Block getBlock(int x, int z, BedrockRandom random, IChunk chunk) {
        return SUGARCANE_BLOCK;
    }
}
