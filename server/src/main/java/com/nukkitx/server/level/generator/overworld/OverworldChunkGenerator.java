package com.nukkitx.server.level.generator.overworld;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.api.block.BlockState;
import com.nukkitx.api.block.BlockTypes;
import com.nukkitx.api.level.Level;
import com.nukkitx.api.level.chunk.Chunk;
import com.nukkitx.server.block.NukkitBlockState;
import com.nukkitx.server.level.generator.AbstractChunkGenerator;
import com.nukkitx.server.level.populator.impl.bedrock.FloorBedrockPopulator;

import java.util.Random;

/**
 * Ported from my from-scratch generator I made on the master branch, with some improvements
 *
 * @author DaPorkchop_
 */
public class OverworldChunkGenerator extends AbstractChunkGenerator {
    private static final BlockState STONE = new NukkitBlockState(BlockTypes.STONE, null, null);
    private static final BlockState WATER = new NukkitBlockState(BlockTypes.STATIONARY_WATER, null, null);
    private static final BlockState AIR = new NukkitBlockState(BlockTypes.AIR, null, null);

    private static final Vector3f SPAWN = new Vector3f(0, 128, 0);

    {
        this.addPopulator(new FloorBedrockPopulator());
    }

    @Override
    public void generateChunk(Level level, Chunk chunk, Random random) {

    }

    @Override
    public Vector3f getDefaultSpawn() {
        return SPAWN;
    }
}
