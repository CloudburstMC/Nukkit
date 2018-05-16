package com.nukkitx.server.level.generator.flat;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.api.block.BlockState;
import com.nukkitx.api.block.BlockTypes;
import com.nukkitx.api.level.Level;
import com.nukkitx.api.level.chunk.Chunk;
import com.nukkitx.api.level.chunk.generator.ChunkGenerator;
import com.nukkitx.server.block.NukkitBlockState;

import java.util.Random;

public class FlatChunkGenerator implements ChunkGenerator {
    private static final BlockState BEDROCK = new NukkitBlockState(BlockTypes.BEDROCK, null, null);
    private static final BlockState DIRT = new NukkitBlockState(BlockTypes.DIRT, null, null);
    private static final BlockState GRASS = new NukkitBlockState(BlockTypes.GRASS_BLOCK, null, null);

    private static final Vector3f SPAWN = new Vector3f(0, 7, 0);

    @Override
    public void generateChunk(Level level, Chunk chunk, Random random) {
        for (int x1 = 0; x1 < 16; x1++) {
            for (int z1 = 0; z1 < 16; z1++) {
                chunk.setBlock(x1, 0, z1, BEDROCK, false);
                for (int y = 1; y < 4; y++) {
                    chunk.setBlock(x1, y, z1, DIRT, false);
                }
                chunk.setBlock(x1, 4, z1, GRASS, false);
            }
        }
    }

    @Override
    public void populateChunk(Level level, Chunk chunk, Random random) {

    }

    @Override
    public Vector3f getDefaultSpawn() {
        return SPAWN;
    }
}
