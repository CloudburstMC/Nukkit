package cn.nukkit.level.generator;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.level.chunk.IChunk;
import cn.nukkit.math.BedrockRandom;
import cn.nukkit.math.Vector3f;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.Map;

@NotThreadSafe
public interface Generator {

    static long getChunkSeed(int chunkX, int chunkZ, long seed) {
        return Chunk.key(chunkX, chunkZ) ^ seed;
    }

    void generateChunk(BedrockRandom random, IChunk chunk);

    void populateChunk(ChunkManager level, BedrockRandom random, int chunkX, int chunkZ);

    Map<String, Object> getSettings();

    Vector3f getSpawn();
}
