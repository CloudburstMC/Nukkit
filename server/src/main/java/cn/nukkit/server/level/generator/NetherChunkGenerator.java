package cn.nukkit.server.level.generator;

import cn.nukkit.api.level.Level;
import cn.nukkit.api.level.chunk.Chunk;
import cn.nukkit.api.level.chunk.generator.ChunkGenerator;
import com.flowpowered.math.vector.Vector3f;

import java.util.Random;

public class NetherChunkGenerator implements ChunkGenerator {

    private static final Vector3f SPAWN = new Vector3f(0, 7, 0);

    public NetherChunkGenerator() {

    }

    @Override
    public void generateChunk(Level level, Chunk chunk, Random random) {

    }

    @Override
    public void populateChunk(Level level, Chunk chunk, Random random) {

    }

    @Override
    public Vector3f getDefaultSpawn() {
        return SPAWN;
    }

}
