package com.nukkitx.api.level.chunk.generator;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.api.level.Level;
import com.nukkitx.api.level.chunk.Chunk;
import com.nukkitx.api.level.data.Dimension;
import com.nukkitx.api.level.data.Generator;

import java.util.Random;

public interface ChunkGenerator {

    void generateChunk(Level level, Chunk chunk, Random random);

    void populateChunk(Level level, Chunk chunk, Random random);

    Vector3f getDefaultSpawn();

    /**
     * The dimension sent to the client which will result in a different loading screen background.
     *
     * @return dimension
     */
    default Dimension getDimension() {
        return Dimension.OVERWORLD;
    }

    /**
     * Generator sent to the client.
     *
     * @return
     */
    default Generator getGenerator() {
        return Generator.UNDEFINED;
    }
}
