package cn.nukkit.api.level.chunk.generator;

import cn.nukkit.api.level.Level;
import cn.nukkit.api.level.chunk.Chunk;
import cn.nukkit.api.level.data.Dimension;
import cn.nukkit.api.level.data.Generator;
import com.flowpowered.math.vector.Vector3f;

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
